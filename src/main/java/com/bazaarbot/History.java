//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

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

    public void registerCommodity(ICommodity good) {
        prices.register(good);
        asks.register(good);
        bids.register(good);
        trades.register(good);
    }

}


