//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;
import com.bazaarbot.PriceBelief;

/**
     * An agent that performs the basic logic from the Doran & Parberry article
     * @author
     */
public class Agent  extends BasicAgent 
{
    //lowest possible price
    public Agent(int id, AgentData data) {
        super(id, data);
    }

    @Override
    public Offer createBid(Market bazaar, ICommodity good, double limit) {
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

    @Override
    public Offer createAsk(Market bazaar, ICommodity commodity_, double limit_) {
        double ask_price = _inventory.query_cost(commodity_) * 1.02;
        //asks are fair prices:  costs + small profit
        double quantity_to_sell = _inventory.query(commodity_);
        //put asks out for all inventory
        if (quantity_to_sell > 0)
        {
            return new Offer(id,commodity_,quantity_to_sell,ask_price);
        }
         
        return null;
    }

    @Override
    public void generateOffers(Market bazaar, ICommodity commodity) {
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
            if (shortage > 0 && space > 0)
            {
                double limit = 0;
                if ((shortage) <= space)
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

    @Override
    public void updatePriceModel(Market bazaar, String act, ICommodity good, boolean success, double unitPrice) {
        if(!goodsPriceBelief.containsKey(good))
            goodsPriceBelief.put(good, new PriceBelief());

        goodsPriceBelief.get(good).addTransaction(unitPrice, success);
    }

}


