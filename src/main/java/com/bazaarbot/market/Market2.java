package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.ContractQuote;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.IHistoryRegistryWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class Market2 {
    private static final Logger LOG = LoggerFactory.getLogger(Market2.class);

    private List<Offer> bidOffers = new ArrayList<>();
    private List<Offer> askOffers = new ArrayList<>();

    //String name, MarketData marketData, ISignalBankrupt isb, IContractResolver contractResolver, Random rng
    public Market2() {
    }

    public void putBid(Offer offer) {
        this.bidOffers.add(offer);
    }

    public void putAsk(Offer offer) {
        this.askOffers.add(offer);
    }

    public void step(IContractResolver contractResolver, IHistoryRegistryWrite registry) {
        // 0. Fill up the registry with current items, before proceed
        registry.addAskOffers(askOffers);
        registry.addBidOffers(bidOffers);

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
        ContractQuote quote = contractResolver.getQuote(this, bidOffer, askOffer);
        double negotiatedPrice = quote.getFinalPrice();
        if (negotiatedPrice == 0) {
            // no deal, price is too high
            return null;
        }
        ICommodity commodity = askOffer.getCommodity();
        IAgent buyer = askOffer.getAgent();
        IAgent seller = bidOffer.getAgent();
        // 4.3 We do have an agreement over there.
        // Now prepare contract

        IContract contract = contractResolver.newContract(seller, buyer, commodity, askAmount, negotiatedPrice);
        registry.addContract(contract);

        // 4.4 Do transfer the money
        seller.setMoneyAvailable(seller.getMoneyAvailable() + negotiatedPrice);
        buyer.setMoneyAvailable(buyer.getMoneyAvailable() - negotiatedPrice);

        // 4.5 Update agent price beliefs based on successful transaction
        //buyer.updatePriceModel("buy", good, true, negotiatedPrice / askOffer.getUnits());
        //seller.updatePriceModel("sell", good, true, negotiatedPrice / askOffer.getUnits());

        // 4.6 Make history records

        return contract;
    }
}