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

    public void bid(Offer offer) {
        if (!bids.containsKey(offer.good)) {
            bids.put(offer.good, new ArrayList<>());
            asks.put(offer.good, new ArrayList<>());
        }

        bids.get(offer.good).add(offer);
    }

    public void ask(Offer offer) {
        if (!bids.containsKey(offer.good)) {
            bids.put(offer.good, new ArrayList<>());
            asks.put(offer.good, new ArrayList<>());
        }
         
        asks.get(offer.good).add(offer);
    }

}


