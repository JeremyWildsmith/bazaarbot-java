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
    //A small scaling of the ask price (to generate profit)
    private static final double ASK_PRICE_INFLATION = 1.02;

    //lowest possible price
    public Agent(AgentData data) {
        super(data);
    }

    @Override
    public Offer createBid(Market bazaar, ICommodity good, double limit) {
        // determinePriceOf(good);  bids are now made "at market", no price determination needed
        int bidPrice = 0;

        double ideal = determinePurchaseQuantity(bazaar,good);
        //can't buy more than limit
        double quantityToBuy = ideal > limit ? limit : ideal;

        if (quantityToBuy > 0)
            return new Offer(this, good, quantityToBuy, bidPrice);
         
        return null;
    }

    @Override
    public Offer createAsk(Market bazaar, ICommodity commodity, double limit) {
        //asks are fair prices:  costs + small profit
        double askPrice = inventory.queryCost(commodity) * ASK_PRICE_INFLATION;
        double quantityToSell = inventory.query(commodity);

        //put asks out for all inventory
        if (quantityToSell > 0)
            return new Offer(this, commodity, quantityToSell, askPrice);
         
        return null;
    }

    @Override
    public void generateOffers(Market bazaar, ICommodity commodity) {
        Offer offer;
        double surplus = inventory.surplus(commodity);
        if (surplus >= 1)
        {
            offer = createAsk(bazaar,commodity,1);
            if (offer != null)
                bazaar.ask(offer);
             
        }
        else
        {
            double shortage = inventory.shortage(commodity);
            double space = inventory.getEmptySpace();

            double limit = Math.min(shortage, space);

            if (limit > 0)
            {
                offer = createBid(bazaar,commodity,limit);

                if (offer != null)
                    bazaar.bid(offer);
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


