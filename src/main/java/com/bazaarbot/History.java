//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

public class History
{
    public HistoryLog prices;
    public HistoryLog asks;
    public HistoryLog bids;
    public HistoryLog trades;
    public HistoryLog profit;
    public History() throws Exception {
        prices = new HistoryLog(EconNoun.Price);
        asks = new HistoryLog(EconNoun.Ask);
        bids = new HistoryLog(EconNoun.Bid);
        trades = new HistoryLog(EconNoun.Trade);
        profit = new HistoryLog(EconNoun.Profit);
    }

    public void register(String good) throws Exception {
        prices.register(good);
        asks.register(good);
        bids.register(good);
        trades.register(good);
        profit.register(good);
    }

}


