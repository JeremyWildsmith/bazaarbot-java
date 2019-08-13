//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import com.github.martincooper.datatable.DataColumn;
import com.github.martincooper.datatable.DataTable;
import com.github.martincooper.datatable.DataTableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradeBook   
{
    public HashMap<String, List<Offer>> bids;
    public HashMap<String,List<Offer>> asks;

    public TradeBook() throws Exception {
        bids = new HashMap<String,List<Offer>>();
        asks = new HashMap<String,List<Offer>>();
    }

    public void register(String name) throws Exception {
        asks.put(name, new ArrayList<Offer>());
        bids.put(name, new ArrayList<Offer>());
    }

    public boolean bid(Offer offer) throws Exception {
        if (!bids.containsKey(offer.good))
            return false;
         
        bids.get(offer.good).add(offer);
        return true;
    }

    public boolean ask(Offer offer) throws Exception {
        if (!bids.containsKey(offer.good))
            return false;
         
        asks.get(offer.good).add(offer);
        return true;
    }

}


