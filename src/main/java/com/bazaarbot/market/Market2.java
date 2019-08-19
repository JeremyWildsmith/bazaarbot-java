package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.contract.DefaultContractResolver;
import com.bazaarbot.contract.IContractResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class Market2 extends Market {
    private List<Offer> bidOffers = new ArrayList<>();
    private List<Offer> askOffers = new ArrayList<>();

    //String name, MarketData marketData, ISignalBankrupt isb, IContractResolver contractResolver, Random rng
    public Market2(MarketData marketData, ISignalBankrupt bankrupt) {
        super("test", marketData, bankrupt, new DefaultContractResolver(), new Random());
    }

    public void putBid(Offer offer) {
        this.bidOffers.add(offer);
    }

    public void putAsk(Offer offer) {
        this.askOffers.add(offer);
    }

    public void startSession(int rounds) {
        for (int i = 0; i < rounds; i++) {
            sessionRound(new DefaultContractResolver());
            // outputs what is left from session
            // e.g. unmet offers
            // next round they will be served in priority, because they were created earlier
            //
            // Put statistics, which affects market prices depending the left offers, quantities from the round and prices
        }
    }

    private void sessionRound(IContractResolver contractResolver) {
        // 1. Get the bids for the round sorted by the time they were created
        List<Offer> providedBids = bidOffers.stream().
                sorted((offer1, offer2) -> offer1.getTimePut() > offer2.getTimePut() ? 1 : 0).
                collect(Collectors.toList());

        // 2. Iterate through them
        for (Offer bidOffer : providedBids) {
            // 3. Find the asks provided by current bid. First reduce the list by searchable good,
            // then sort by the time they were created
            List<Offer> interestedAsks = askOffers.stream().
                    takeWhile(offer -> offer.getGood().equals(bidOffer.getGood())).
                    sorted((offer1, offer2) -> offer1.getTimePut() > offer2.getTimePut() ? 1 : 0).
                    collect(Collectors.toList());
            // 4. Iterate and try to make a deal
            for (Offer askOffer : interestedAsks) {
                boolean success = tryDeal(bidOffer, askOffer, askOffer.getGood(), contractResolver);
                // 5. If the deal was done make record in transactions and remove corresponding offers for next round
                // Maybe we need to check here if the bid is fully fulfilled,
                // e.g. all quantity were sold or something left for next round?
                if (success) {
                    bidOffers.remove(bidOffer);
                    askOffers.remove(askOffer);
                }
            }
        }
    }

    private boolean tryDeal(Offer bidOffer, Offer askOffer, ICommodity good, IContractResolver contractResolver) {
        // 4.1 Find if anyway seller can provide the quantity
        double quantityTraded = Math.min(bidOffer.getUnits(), askOffer.getUnits());
        if (quantityTraded <= 0) {
            // nothing to get from seller, no items
            return false;
        }
        // 4.2 Negotiate the price by some engine (separate method)
        double negotiatedPrice = negotiatePrice(bidOffer, askOffer, quantityTraded);
        if (negotiatedPrice <= 0) {
            // no deal, price is too high
            return false;
        }
        BasicAgent buyer = askOffer.getAgent();
        BasicAgent seller = bidOffer.getAgent();
        // 4.3 We do have an agreement over there.
        // Now prepare contract

        // should return something I guess?
        contractResolver.newContract(seller, buyer, askOffer.getGood(), quantityTraded, negotiatedPrice);

        // 4.4 Do transfer the money
        seller.setMoneyAvailable(seller.getMoneyAvailable() + negotiatedPrice);
        buyer.setMoneyAvailable(buyer.getMoneyAvailable() - negotiatedPrice);

        // 4.5 Update agent price beliefs based on successful transaction
        buyer.updatePriceModel(this, "buy", good, true, negotiatedPrice / quantityTraded);
        seller.updatePriceModel(this, "sell", good, true, negotiatedPrice / quantityTraded);

        // 4.6 Make history records

        return true;

    }

    private double negotiatePrice(Offer bidOffer, Offer askOffer, double quantityTraded) {
        // 4.2.1 Negotiate the price - different task
        // history should affect this
        return bidOffer.getUnitPrice();
    }

}
