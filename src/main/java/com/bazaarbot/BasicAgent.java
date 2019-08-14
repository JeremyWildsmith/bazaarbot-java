//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BasicAgent
{
    public int id;
    //unique integer identifier
    private String className;
    private double money;
    //dfs stub  needed?
    public double moneyLastRound;
    //dfs stub needed?
    public double trackcosts;
    private Logic _logic;
    protected Inventory _inventory;
    protected HashMap<String, PriceBelief> goodsPriceBelief = new HashMap<>();
    //profit from last round
    private int _lookback = 15;



    public BasicAgent(int id, AgentData data) {
        this.id = id;
        setClassName(data.className);
        setMoney(data.money);
        _inventory = new Inventory();
        _inventory.fromData(data.inventory);
        _logic = data.logic;
        if (data.lookBack == null)
        {
            _lookback = 15;
        }
        else
        {
            _lookback = data.lookBack;
        }
        trackcosts = 0;
    }

    public void simulate(Market market) {
        _logic.perform(this,market);
    }

    public abstract void generateOffers(Market bazaar, String good);
    public abstract void updatePriceModel(Market bazaar, String act, String good, boolean success, double unitPrice);
    public abstract Offer createBid(Market bazaar, String good, double limit);
    public abstract Offer createAsk(Market bazaar, String commodity_, double limit_);

    public final double queryInventory(String good) {
        return _inventory.query(good);
    }

    public final void produceInventory(String good, double delta) {
        if (trackcosts < 1)
            trackcosts = 1;
         
        double curunitcost = _inventory.change(good,delta,trackcosts / delta);
        trackcosts = 0;
    }

    public final void consumeInventory(String good, double delta) {
        if (good.compareTo("money") == 0)
        {
            setMoney(getMoney() + delta);
            if (delta < 0)
                trackcosts += (-delta);
             
        }
        else
        {
            double curunitcost = _inventory.change(good,delta,0);
            if (delta < 0)
                trackcosts += (-delta) * curunitcost;
             
        } 
    }

    public final void changeInventory(String good, double delta, double unit_cost) {
        if (good.compareTo("money") == 0)
        {
            setMoney(getMoney() + delta);
        }
        else
        {
            _inventory.change(good,delta,unit_cost);
        } 
    }

    protected double determineSaleQuantity(Market bazaar, String commodity_) {
        Double mean = bazaar.getAverageHistoricalPrice(commodity_,_lookback);
        //double
        Point trading_range = observeTradingRange(commodity_,10);
        //point
        if (trading_range != null && mean > 0)
        {
            double favorability = Quick.positionInRange(mean, trading_range.x, trading_range.y);
            //double
            //position_in_range: high means price is at a high point
            double amount_to_sell = Math.round(favorability * _inventory.surplus(commodity_));
            //double
            amount_to_sell = _inventory.query(commodity_);
            if (amount_to_sell < 1)
            {
                amount_to_sell = 1;
            }
             
            return amount_to_sell;
        }
         
        return 0;
    }

    protected double determinePurchaseQuantity(Market bazaar, String commodity_) {
        Double mean = bazaar.getAverageHistoricalPrice(commodity_,_lookback);
        //double
        Point trading_range = observeTradingRange(commodity_,10);
        //Point
        if (trading_range != null)
        {
            double favorability = Quick.positionInRange(mean, trading_range.x, trading_range.y);
            //double
            favorability = 1 - favorability;
            //do 1 - favorability to see how close we are to the low end
            double amount_to_buy = Math.round(favorability * _inventory.shortage(commodity_));
            //double
            if (amount_to_buy < 1)
            {
                amount_to_buy = 1;
            }
             
            return amount_to_buy;
        }
         
        return 0;
    }

    private Point observeTradingRange(String good, int window) {
        if(!goodsPriceBelief.containsKey(good))
            goodsPriceBelief.put(good, new PriceBelief());

        return goodsPriceBelief.get(good).observe(window);
    }

    public final void updatePriceModel(Market market, String buy, String good, boolean b) {
        updatePriceModel(market, buy, good, b, 0);
    }

    public boolean isInventoryFull() {
        return _inventory.getEmptySpace() == 0;
    }

    public double get_profit() {
        return getMoney() - moneyLastRound;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String value) {
        className = value;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double value) {
        money = value;
    }
}


