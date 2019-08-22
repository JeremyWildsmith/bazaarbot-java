//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import com.bazaarbot.market.Offer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradeBook {
    private final HashMap<ICommodity, List<Offer>> bids;
    private final HashMap<ICommodity, List<Offer>> asks;

    public TradeBook() {
        bids = new HashMap<>();
        asks = new HashMap<>();
    }

    public void register(ICommodity name) {
        asks.put(name, new ArrayList<>());
        bids.put(name, new ArrayList<>());
    }

    public void bid(Offer offer) {
        if (!bids.containsKey(offer.getCommodity())) {
            bids.put(offer.getCommodity(), new ArrayList<>());
            asks.put(offer.getCommodity(), new ArrayList<>());
        }

        bids.get(offer.getCommodity()).add(offer);
    }

    public void ask(Offer offer) {
        if (!bids.containsKey(offer.getCommodity())) {
            bids.put(offer.getCommodity(), new ArrayList<>());
            asks.put(offer.getCommodity(), new ArrayList<>());
        }

        asks.get(offer.getCommodity()).add(offer);
    }

    public HashMap<ICommodity, List<Offer>> getBids() {
        return bids;
    }

    public HashMap<ICommodity, List<Offer>> getAsks() {
        return asks;
    }
}


