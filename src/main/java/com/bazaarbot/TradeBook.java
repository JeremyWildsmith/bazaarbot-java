//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import com.bazaarbot.market.Offer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradeBook   
{
    public HashMap<ICommodity, List<Offer>> bids;
    public HashMap<ICommodity, List<Offer>> asks;

    public TradeBook() {
        bids = new HashMap<>();
        asks = new HashMap<>();
    }

    public void register(ICommodity name) {
        asks.put(name, new ArrayList<>());
        bids.put(name, new ArrayList<>());
    }

    public boolean bid(Offer offer) {
        if (!bids.containsKey(offer.good))
            return false;
         
        bids.get(offer.good).add(offer);
        return true;
    }

    public boolean ask(Offer offer) {
        if (!bids.containsKey(offer.good))
            return false;
         
        asks.get(offer.good).add(offer);
        return true;
    }

}


