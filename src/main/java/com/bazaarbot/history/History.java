//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.history;

import com.bazaarbot.ICommodity;

import java.util.*;

public class History {
    private HistoryLog<ICommodity> prices;
    private HistoryLog<ICommodity> asks;
    private HistoryLog<ICommodity> bids;
    private HistoryLog<ICommodity> trades;
    private HistoryLog<String> profit;

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

    public  List<ICommodity> getCommodities() {
        Set<ICommodity> result = new HashSet<>();

        List<HistoryLog<ICommodity>> sources = new ArrayList<>();
        sources.add(prices);
        sources.add(asks);
        sources.add(bids);
        sources.add(trades);

        for (HistoryLog<ICommodity> historyItem : sources) {
            result.addAll(historyItem.getSubjects());
        }

        return new ArrayList<>(result);
    }

    public HistoryLog<ICommodity> getPrices() {
        return prices;
    }

    public void setPrices(HistoryLog<ICommodity> prices) {
        this.prices = prices;
    }

    public HistoryLog<ICommodity> getAsks() {
        return asks;
    }

    public void setAsks(HistoryLog<ICommodity> asks) {
        this.asks = asks;
    }

    public HistoryLog<ICommodity> getBids() {
        return bids;
    }

    public void setBids(HistoryLog<ICommodity> bids) {
        this.bids = bids;
    }

    public HistoryLog<ICommodity> getTrades() {
        return trades;
    }

    public void setTrades(HistoryLog<ICommodity> trades) {
        this.trades = trades;
    }

    public HistoryLog<String> getProfit() {
        return profit;
    }

    public void setProfit(HistoryLog<String> profit) {
        this.profit = profit;
    }
}


