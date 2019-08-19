//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.Logic;
import com.bazaarbot.PriceRange;
import com.bazaarbot.inventory.Inventory;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;
import com.bazaarbot.PriceBelief;

import java.util.HashMap;

/**
     * An agent that performs the basic logic from the Doran & Parberry article
     * @author
     */
public class DefaultAgent implements IAgent
{
    //A small scaling of the ask price (to generate profit)
    private static final double ASK_PRICE_INFLATION = 1.02;

    private String className;
    private double money;
    //dfs stub  needed?
    private double moneyLastRound;
    //dfs stub needed?
    private double trackcosts;
    private Logic _logic;
    private Inventory inventory;
    private HashMap<ICommodity, PriceBelief> goodsPriceBelief = new HashMap<>();
    //profit from last round
    private int _lookback = 15;

    //lowest possible price
    public DefaultAgent(AgentData data) {
        this.className = data.getClassName();
        this.money = data.getMoney();
        setMoney(data.getMoney());
        inventory = new Inventory();
        inventory.fromData(data.getInventory());
        _logic = data.getLogic();

        if (data.getLookBack() != null)
            _lookback = data.getLookBack();

        trackcosts = 0;
    }

    public void simulate(Market market) {
        _logic.perform(this,market);
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
    public void updatePriceModel(String act, ICommodity good, boolean success, double unitPrice) {
        if(!goodsPriceBelief.containsKey(good))
            goodsPriceBelief.put(good, new PriceBelief());

        goodsPriceBelief.get(good).addTransaction(unitPrice, success);
    }

    public final double queryInventory(ICommodity good) {
        return inventory.query(good);
    }

    public final void produceInventory(ICommodity good, double delta) {
        if (trackcosts < 1)
            trackcosts = 1;

        double curunitcost = inventory.change(good,delta,trackcosts / delta);
        trackcosts = 0;
    }

    public final void consumeInventory(ICommodity good, double delta) {
        if (good.getName().compareTo("money") == 0)
        {
            setMoney(getMoney() + delta);
            if (delta < 0)
                trackcosts += (-delta);

        }
        else
        {
            double curunitcost = inventory.change(good,delta,0);
            if (delta < 0)
                trackcosts += (-delta) * curunitcost;

        }
    }

    public final void changeInventory(ICommodity good, double delta, double unit_cost) {
        if (good.getName().compareTo("money") == 0)
        {
            setMoney(getMoney() + delta);
        }
        else
        {
            inventory.change(good,delta,unit_cost);
        }
    }

    protected double determinePurchaseQuantity(Market bazaar, ICommodity commodity_) {
        Double mean = bazaar.getAverageHistoricalPrice(commodity_,_lookback);
        //double
        PriceRange trading_range = observeTradingRange(commodity_,10);
        //Point
        if (trading_range != null)
        {
            double favorability = trading_range.positionInRange(mean);
            //double
            favorability = 1 - favorability;
            //do 1 - favorability to see how close we are to the low end
            double amount_to_buy = Math.round(favorability * inventory.shortage(commodity_));
            //double
            if (amount_to_buy < 1)
            {
                amount_to_buy = 1;
            }

            return amount_to_buy;
        }

        return 0;
    }

    private PriceRange observeTradingRange(ICommodity good, int window) {
        if(!goodsPriceBelief.containsKey(good))
            goodsPriceBelief.put(good, new PriceBelief());

        return goodsPriceBelief.get(good).observe(window);
    }

    public AgentSnapshot getSnapshot() {
        return new AgentSnapshot(getClassName(), getMoney(), new Inventory(inventory));
    }

    public boolean isInventoryFull() {
        return inventory.getEmptySpace() == 0;
    }

    public double get_profit() {
        return getMoney() - moneyLastRound;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public double getMoney() {
        return money;
    }

    @Override
    public void setMoney(double value) {
        money = value;
    }

    @Override
    public void setMoneyLastRound(double value) {
        moneyLastRound = value;
    }
}


