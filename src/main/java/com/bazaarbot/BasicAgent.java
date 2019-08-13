//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasicAgent   
{
    public int id;
    //unique integer identifier
    private String className;
    private double money;
    private double nProduct;
    public boolean destroyed;
    //dfs stub  needed?
    public double moneyLastRound;
    //dfs stub needed?
    public double profit;
    //dfs stub needed?
    public double trackcosts;
    private Logic _logic;
    protected Inventory _inventory;
    protected HashMap<String, List<Double>> _observedTradingRange;
    private double _profit = 0;
    //profit from last round
    private int _lookback = 15;



    public BasicAgent(int id, AgentData data) throws Exception {
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
            _lookback = (int)data.lookBack;
        } 
        _observedTradingRange = new HashMap<String, List<Double>>();
        trackcosts = 0;
    }

    public void destroy() throws Exception {
        destroyed = true;
        _inventory.destroy();
        for (String key : _observedTradingRange.keySet())
        {
            List<Double> list = _observedTradingRange.get(key);
            list.clear();
        }
        _observedTradingRange.clear();
        _observedTradingRange = null;
        _logic = null;
    }

    public void init(Market market) throws Exception {
        List<String> listGoods = market.getGoods_unsafe();
        for (String str : listGoods)
        {
            //List<String>
            List<Double> trades = new ArrayList<Double>();
            int price = 2;
            // market.getAverageHistoricalPrice(str, _lookback);
            trades.add(price * 1.0);
            trades.add(price * 3.0);
            //push two fake trades to generate a range
            //set initial price belief & observed trading range
            _observedTradingRange.put(str, trades);
        }
    }

    public void simulate(Market market) throws Exception {
        _logic.perform(this,market);
    }

    public void generateOffers(Market bazaar, String good) throws Exception {
    }

    //no implemenation -- provide your own in a subclass
    public void updatePriceModel(Market bazaar, String act, String good, boolean success, double unitPrice) throws Exception {
    }

    //no implementation -- provide your own in a subclass
    public Offer createBid(Market bazaar, String good, double limit) throws Exception {
        return null;
    }

    //no implementation -- provide your own in a subclass
    public Offer createAsk(Market bazaar, String commodity_, double limit_) throws Exception {
        return null;
    }

    //no implementation -- provide your own in a subclass
    public double queryInventory(String good) throws Exception {
        return _inventory.query(good);
    }

    public void produceInventory(String good, double delta) throws Exception {
        if (trackcosts < 1)
            trackcosts = 1;
         
        double curunitcost = _inventory.change(good,delta,trackcosts / delta);
        trackcosts = 0;
    }

    public void consumeInventory(String good, double delta) throws Exception {
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

    public void changeInventory(String good, double delta, double unit_cost) throws Exception {
        if (good.compareTo("money") == 0)
        {
            setMoney(getMoney() + delta);
        }
        else
        {
            _inventory.change(good,delta,unit_cost);
        } 
    }



    protected double determineSaleQuantity(Market bazaar, String commodity_) throws Exception {
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

    protected double determinePurchaseQuantity(Market bazaar, String commodity_) throws Exception {
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

    private Point observeTradingRange(String good, int window) throws Exception {
        List<Double> a = _observedTradingRange.get(good);
        //List<double>
        Point pt = new Point(Quick.minArr(a,window),Quick.maxArr(a,window));
        return pt;
    }

    public final void updatePriceModel(Market market, String buy, String good, boolean b) throws Exception {
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

    public void setnProduct(double value) {
        nProduct = value;
    }
}


