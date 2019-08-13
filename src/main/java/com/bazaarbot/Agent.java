//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;

import java.util.List;

/**
     * An agent that performs the basic logic from the Doran & Parberry article
     * @author
     */
public class Agent  extends BasicAgent 
{
    public static double MIN_PRICE = 0.01;
    //lowest possible price
    public Agent(int id, AgentData data) throws Exception {
        super(id, data);
    }

    public Offer createBid(Market bazaar, String good, double limit) throws Exception {
        int bidPrice = 0;
        // determinePriceOf(good);  bids are now made "at market", no price determination needed
        Double ideal = determinePurchaseQuantity(bazaar,good);
        //can't buy more than limit
        double quantityToBuy = ideal > limit ? limit : ideal;
        if (quantityToBuy > 0)
        {
            return new Offer(id,good,quantityToBuy,bidPrice);
        }
         
        return null;
    }

    public Offer createAsk(Market bazaar, String commodity_, double limit_) throws Exception {
        Double ask_price = _inventory.query_cost(commodity_) * 1.02;
        //asks are fair prices:  costs + small profit
        Double quantity_to_sell = _inventory.query(commodity_);
        //put asks out for all inventory
        setnProduct(quantity_to_sell);
        if (quantity_to_sell > 0)
        {
            return new Offer(id,commodity_,quantity_to_sell,ask_price);
        }
         
        return null;
    }

    public void generateOffers(Market bazaar, String commodity) throws Exception {
        Offer offer;
        double surplus = _inventory.surplus(commodity);
        if (surplus >= 1)
        {
            offer = createAsk(bazaar,commodity,1);
            if (offer != null)
            {
                bazaar.ask(offer);
            }
             
        }
        else
        {
            Double shortage = _inventory.shortage(commodity);
            Double space = _inventory.getEmptySpace();
            Double unit_size = _inventory.getCapacityFor(commodity);
            if (shortage > 0 && space >= unit_size)
            {
                double limit = 0;
                if ((shortage * unit_size) <= space)
                {
                    //enough space for ideal order
                    limit = shortage;
                }
                else
                {
                    //not enough space for ideal order
                    limit = space;
                } 
                // Math.Floor(space / shortage);
                if (limit > 0)
                {
                    offer = createBid(bazaar,commodity,limit);
                    if (offer != null)
                    {
                        bazaar.bid(offer);
                    }
                     
                }
                 
            }
             
        } 
    }

    public void updatePriceModel(Market bazaar, String act, String good, boolean success, double unitPrice) throws Exception {
        List<Double> observed_trades;
        if (success)
        {
            //Add this to my list of observed trades
            observed_trades = _observedTradingRange.get(good);
            observed_trades.add(unitPrice);
        }
         
        Double public_mean_price = bazaar.getAverageHistoricalPrice(good,1);
    }

}


