//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.history;

import com.bazaarbot.EconNoun;
import com.bazaarbot.ICommodity;

import java.util.*;

public class History
{
    public HistoryLog<ICommodity> prices;
    public HistoryLog<ICommodity> asks;
    public HistoryLog<ICommodity> bids;
    public HistoryLog<ICommodity> trades;
    public HistoryLog<String> profit;

    public History() {
        prices = new HistoryLog<>(EconNoun.Price);
        asks = new HistoryLog<>(EconNoun.Ask);
        bids = new HistoryLog<>(EconNoun.Bid);
        trades = new HistoryLog<>(EconNoun.Trade);
        profit = new HistoryLog<>(EconNoun.Profit);
    }

    public History(History src) {
        prices = new HistoryLog<>(src.prices);
        asks = new HistoryLog<>(src.asks);
        bids = new HistoryLog<>(src.bids);
        trades = new HistoryLog<>(src.trades);
        profit = new HistoryLog<>(src.profit);
    }

    public void registerCommodity(ICommodity good) {
        prices.register(good);
        asks.register(good);
        bids.register(good);
        trades.register(good);
    }

    public ICommodity[] getCommodities() {
        Set<ICommodity> result = new HashSet<>();

        List<HistoryLog<ICommodity>> sources = new ArrayList<>();
        sources.add(prices);
        sources.add(asks);
        sources.add(bids);
        sources.add(trades);

        for(HistoryLog<ICommodity> l : sources) {
            Collections.addAll(result, l.getSubjects(new ICommodity[0]));
        }

        return result.toArray(new ICommodity[0]);
    }

}


