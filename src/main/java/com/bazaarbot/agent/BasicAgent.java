package com.bazaarbot.agent;

import com.bazaarbot.Point2F;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BasicAgent {

    /********PRIVATE************/

    protected Logic _logic;
    protected Inventory _inventory;
    protected Map<String, Point2F> _priceBeliefs;
    protected Map<String, List<Float>> _observedTradingRange;
    protected float _profit = 0;	//profit from last round
    protected int _lookback = 15;

    public int id;				//unique integer identifier
    public String className;	//string identifier, "famer", "woodcutter", etc.
    public float money;
    public Float moneyLastRound;
    public Float profit;
    public Float inventorySpace;
    public Bool inventoryFull;
    public Bool destroyed;

    public BasicAgent(int id, AgentData data)
    {

        this.id = id;
        className = data.className;
        money = data.money;
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

        _priceBeliefs = new HashMap<String, Point2F>();
        _observedTradingRange = new HashMap<String, List<Float>>();
    }

    public void init(Market market)
    {
        List<String> listGoods = market.getGoods_unsafe();
        for (String str : listGoods)
        {
            List<Float> trades = new ArrayList<Float>();

            float price = market.getAverageHistoricalPrice(str, _lookback);
            trades.add(price * 0.5f);
            trades.add(price * 1.5f);	//push two fake trades to generate a range

            //set initial price belief & observed trading range
            _observedTradingRange.put(str, trades);
            _priceBeliefs.put(str, new Point2F(price * 0.5f, price * 1.5f));
        }
    }

    public void simulate(Market market)
    {
        _logic.perform(this, market);
    }

    public void generateOffers(Market market, String good)
    {
        //no implemenation -- provide your own in a subclass
    }

    public void updatePriceModel(Market bazaar, String act, String good, boolean success, float unitPrice)
    {
        //no implementation -- provide your own in a subclass
    }

    public Offer createBid(Market bazaar, String good, float limit)
    {
        //no implementation -- provide your own in a subclass
        return null;
    }

    public Offer createAsk(Market bazaar, String commodity_, float limit_)
    {
        //no implementation -- provide your own in a subclass
        return null;
    }

    public float queryInventory(String good)
    {
        return _inventory.query(good);
    }

    public void changeInventory(String good, float delta)
    {
        _inventory.change(good, delta);
    }

    private float get_inventorySpace()
    {
        return _inventory.getEmptySpace();
    }

    public boolean get_inventoryFull()
    {
        return _inventory.getEmptySpace() == 0;
    }

    protected float get_profit()
    {
        return money - moneyLastRound;
    }

    protected float determinePriceOf(String commodity_)
    {
        Point2F belief = _priceBeliefs.get(commodity_);
        return Quick.randomRange(belief.x, belief.y);
    }

    protected float determineSaleQuantity(Market bazaar, String commodity_)
    {
        float mean = bazaar.getAverageHistoricalPrice(commodity_,_lookback);
        Point2F trading_range = observeTradingRange(commodity_);
        if (trading_range != null)
        {
            float favorability = Quick.positionInRange(mean, trading_range.x, trading_range.y);
            //position_in_range: high means price is at a high point

            float amount_to_sell = Math.round(favorability * _inventory.surplus(commodity_));
            if (amount_to_sell < 1)
            {
                amount_to_sell = 1;
            }
            return amount_to_sell;
        }
        return 0;
    }

    protected float determinePurchaseQuantity(Market bazaar, String commodity_)
    {
        float mean = bazaar.getAverageHistoricalPrice(commodity_,_lookback);
        Point2F trading_range = observeTradingRange(commodity_);

        if (trading_range != null)
        {
            float favorability = Quick.positionInRange(mean, trading_range.x, trading_range.y);
            favorability = 1 - favorability;
            //do 1 - favorability to see how close we are to the low end

            float amount_to_buy = Math.round(favorability * _inventory.shortage(commodity_));
            if (amount_to_buy < 1)
            {
                amount_to_buy = 1;
            }
            return amount_to_buy;
        }
        return 0;
    }

    protected Point2F getPriceBelief(String good)
    {
        return _priceBeliefs.get(good);
    }

    protected Point2F observeTradingRange(String good)
    {
        List<Float> a = _observedTradingRange.get(good);
        Point2F pt = new Point2F(Quick.minArr(a), Quick.maxArr(a));
        return pt;
    }
}