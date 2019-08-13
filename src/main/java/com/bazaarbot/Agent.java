package com.bazaarbot;

import com.bazaarbot.agent.AgentData;
import com.bazaarbot.agent.BasicAgent;

import java.util.ArrayList;
import java.util.List;

public class Agent extends BasicAgent
{
    public static final float SIGNIFICANT = 0.25f;		//25% more or less is "significant"
    public static final float SIG_IMBALANCE = 0.33f;
    public static final float LOW_INVENTORY = 0.1f;		//10% of ideal inventory = "LOW"
    public static final float HIGH_INVENTORY = 2.0f;	//200% of ideal inventory = "HIGH"

    public static final float MIN_PRICE = 0.01f;		//lowest possible price

    public Agent(int id, AgentData data)
    {
        super(id, data);
    }

    @Override 
    public Offer createBid(Market bazaar, String good, float limit)
    {
        float bidPrice = determinePriceOf(good);
        float ideal = determinePurchaseQuantity(bazaar, good);

        //can't buy more than limit
        float quantityToBuy = ideal > limit ? limit : ideal;
        if (quantityToBuy > 0)
        {
            return new Offer(id, good, quantityToBuy, bidPrice);
        }
        return null;
    }

    @Override
    public Offer createAsk(Market bazaar, String commodity_, float limit)
    {
        float ask_price = determinePriceOf(commodity_);
        float ideal = determineSaleQuantity(bazaar, commodity_);

        //can't sell less than limit
        float quantity_to_sell = ideal < limit_ ? limit_ : ideal;
        if (quantity_to_sell > 0)
        {
            return new Offer(id, commodity_, quantity_to_sell, ask_price);
        }
        return null;
    }

    @Override
    public void generateOffers(Market bazaar, String commodity)
    {
        Offer offer;
        float surplus = _inventory.surplus(commodity);
        if (surplus >= 1)
        {
            offer = createAsk(bazaar, commodity, 1);
            if (offer != null)
            {
                bazaar.ask(offer);
            }
        }
        else
        {
            float shortage = _inventory.shortage(commodity);
            float space = _inventory.getEmptySpace();
            float unit_size = _inventory.getCapacityFor(commodity);

            if (shortage > 0 && space >= unit_size)
            {
                float limit = 0;
                if ((shortage * unit_size) <= space)	//enough space for ideal order
                {
                    limit = shortage;
                }
                else									//not enough space for ideal order
                {
                    limit = (float)Math.floor(space / shortage);
                }

                if (limit > 0)
                {
                    offer = createBid(bazaar, commodity, limit);
                    if (offer != null)
                    {
                        bazaar.bid(offer);
                    }
                }
            }
        }
    }

    @Override
    public void updatePriceModel(Market bazaar, String act, String good, boolean success, float unitPrice)
    {
        List<Float> observed_trades = new ArrayList<Float>();

        if (success)
        {
            //Add this to my list of observed trades
            observed_trades = _observedTradingRange.get(good);
            observed_trades.push(unitPrice);
        }

        float public_mean_price = bazaar.getAverageHistoricalPrice(good, 1);

        Point2F belief = getPriceBelief(good);
        float mean = (belief.x + belief.y) / 2;
        float wobble = 0.05f;

        float delta_to_mean = mean - public_mean_price;

        if (success)
        {
            if (act == "buy" && delta_to_mean > SIGNIFICANT)			//overpaid
            {
                belief.x -= delta_to_mean / 2;							//SHIFT towards mean
                belief.y -= delta_to_mean / 2;
            }
            else if (act == "sell" && delta_to_mean < -SIGNIFICANT)		//undersold
            {
                belief.x -= delta_to_mean / 2;							//SHIFT towards mean
                belief.y -= delta_to_mean / 2;
            }

            belief.x += wobble * mean;	//increase the belief's certainty
            belief.y -= wobble * mean;
        }
        else
        {
            throw new RuntimeException("This code makes no sense...");
            /*
            belief.x -= dagentelta_to_mean / 2;	//SHIFT towards the mean
            belief.y -= dagentelta_to_mean / 2;

            var special_cagentase:Bool = false;
            var stocks:Flagentoat = queryInventory(good);
            var ideal:Float = _inventory.ideal(good);

            if (act == "buy" && stocks < LOW_INVENTORY * ideal)
            {
                //very low on inventory AND can't buy
                wobble *= 2;			//bid more liberally
                special_case = true;
            }
            else if (act == "sell" && stocks > HIGH_INVENTORY * ideal)
            {
                //very high on inventory AND can't sell
                wobble *= 2;			//ask more liberally
                special_case = true;
            }

            if (!special_case)
            {
                //Don't know what else to do? Check supply vs. demand
                var asks:Float = bazaar.history.asks.average(good,1);
                var bids:Float = bazaar.history.bids.average(good,1);

                //supply_vs_demand: 0=balance, 1=all supply, -1=all demand
                var supply_vs_demand:Float = (asks - bids) / (asks + bids);

                //too much supply, or too much demand
                if (supply_vs_demand > SIG_IMBALANCE || supply_vs_demand < -SIG_IMBALANCE)
                {
                    //too much supply: lower price
                    //too much demand: raise price

                    var new_mean = public_mean_price * (1 - supply_vs_demand);
                    delta_to_mean = mean - new_mean;

                    belief.x -= delta_to_mean / 2;	//SHIFT towards anticipated new mean
                    belief.y -= delta_to_mean / 2;
                }
            }

            belief.x -= wobble * mean;	//decrease the belief's certainty
            belief.y += wobble * mean;
            */

        }

        if (belief.x < MIN_PRICE)
        {
            belief.x = MIN_PRICE;
        }
        else if (belief.y < MIN_PRICE)
        {
            belief.y = MIN_PRICE;
        }
    }
}
