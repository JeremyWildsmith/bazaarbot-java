package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.TimerHelper;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.contract.IContractNegotiator;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.events.contracts.ContractSignedEvent;
import com.bazaarbot.history.IHistoryRegistryWrite;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class DefaultMarket implements IMarket {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMarket.class);
    private final TimerHelper timerHelper = new TimerHelper();
    private final String name;

    private final List<Offer> bidOffers = new ArrayList<>();
    private final List<Offer> askOffers = new ArrayList<>();

    private List<Offer> newBidOffers = new ArrayList<>();
    private List<Offer> newAskOffers = new ArrayList<>();

    public DefaultMarket() {
        this("DefaultMarket");
    }

    public DefaultMarket(String name) {
        this.name = name;
    }

    @Override
    public void putBid(Offer offer) {
        this.newBidOffers.add(offer);
    }

    @Override
    public void putAsk(Offer offer) {
        this.newAskOffers.add(offer);
    }

    @Override
    public int getBidOffersSize() {
        return bidOffers.size();
    }

    @Override
    public int getAskOffersSize() {
        return askOffers.size();
    }

    @Override
    public void step(IContractResolver contractResolver, IHistoryRegistryWrite registry, EventBus userEventBus) {
        timerHelper.start();
        // 0. Fill up the registry with current items, before proceed
        registry.addAskOffers(newAskOffers);
        registry.addBidOffers(newBidOffers);

        this.bidOffers.addAll(newBidOffers);
        this.askOffers.addAll(newAskOffers);
        this.newBidOffers = new ArrayList<>();
        this.newAskOffers = new ArrayList<>();


        LOG.debug("Starting step. Bids: {} Asks: {}", bidOffers.size(), askOffers.size());

        // 1. Get the bids for the round sorted by the time they were created
        List<Offer> providedBids = bidOffers.stream().
                sorted((offer1, offer2) -> offer1.getCreatedTimeOffer() > offer2.getCreatedTimeOffer() ? 1 : 0).
                collect(Collectors.toList());

        // 2. Iterate through them
        for (Offer bidOffer : providedBids) {
            // 3. Find the asks provided by current bid. First reduce the list by searchable good,
            // then sort by the time they were created
            LOG.debug("Trying bid: {}", bidOffer);
            List<Offer> interestedAsks = askOffers.stream()
                    .filter(offer -> offer.getCommodity().equals(bidOffer.getCommodity()))
                    .sorted((offer1, offer2) -> offer1.getCreatedTimeOffer() > offer2.getCreatedTimeOffer() ? 1 : 0)
                    .collect(Collectors.toList());
            // 4. Iterate and try to make a deal
            for (Offer askOffer : interestedAsks) {
                LOG.debug("---- with ask: {}", askOffer);
                IContract contract = tryDeal(bidOffer, askOffer, contractResolver, registry);
                // 5. If the deal was done make record in transactions and remove corresponding offers for next round
                if (contract != null) {
                    userEventBus.post(new ContractSignedEvent(contract));
                    double quantityTraded = contract.getQuantityTraded();
                    double leftGoodsInBid = bidOffer.getUnits() - quantityTraded;
                    LOG.debug("---- Signed contract for {} x {} @ {}", askOffer.getCommodity(), quantityTraded, contract.getContractPrice());
                    LOG.debug("---- Left goods in bid: {}", leftGoodsInBid);
                    if (leftGoodsInBid == 0) {
                        bidOffers.remove(bidOffer);
                        askOffers.remove(askOffer);
                    } else {
                        // 5.1 We leave the Bid, because not all goods were sold
                        bidOffer.setUnits(leftGoodsInBid);
                        askOffers.remove(askOffer);
                    }
                } else {
                    LOG.debug("---- No contract agreement.");
                }
            }
        }
        timerHelper.stop();
        LOG.debug("Step took {}ns", timerHelper.getTimeNanos());
    }

    private IContract tryDeal(Offer bidOffer, Offer askOffer,
                              IContractResolver contractResolver, IHistoryRegistryWrite registry) {
        // 4.1 Find if anyway seller can provide the quantity
        double bidQuantity = bidOffer.getUnits();
        double askAmount = askOffer.getUnits();
        if (bidQuantity < askAmount) {
            LOG.debug("---- Not enough amount in bid offer. Leaving.");
            // nothing to get from seller, no items
            return null;
        }
        // 4.2 Negotiate the price by some engine (separate method)
        IContractNegotiator quote = contractResolver.getContractNegotiator(this, bidOffer, askOffer);
        BigDecimal negotiatedPrice = quote.negotiateContractPrice();
        if (negotiatedPrice.compareTo(BigDecimal.ZERO) == 0) {
            // no deal, price is too high
            return null;
        }
        ICommodity commodity = askOffer.getCommodity();
        IAgent buyer = askOffer.getAgent();
        IAgent seller = bidOffer.getAgent();
        // 4.3 We do have an agreement over there.
        // Now prepare contract

        IContract contract = contractResolver.signContract(seller, buyer, commodity, askAmount, negotiatedPrice);
        registry.addContract(contract);

        // 4.4 Do transfer the money
        seller.setMoneyAvailable(seller.getMoneyAvailable().add(negotiatedPrice));
        buyer.setMoneyAvailable(buyer.getMoneyAvailable().subtract(negotiatedPrice));

        return contract;
    }

    @Override
    public String toString() {
        return name;
    }
}