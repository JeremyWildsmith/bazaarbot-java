//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.*;
import com.bazaarbot.inventory.Inventory;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

import java.util.HashMap;
import java.util.UUID;

public abstract class BasicAgent {
    private final int id = UUID.randomUUID().hashCode();
    //unique integer identifier
    private final String agentName;
    private double moneyAvailable;
    //dfs stub  needed?
    private double moneyLastRound;
    //dfs stub needed?
    private double trackCosts;
    private Logic logic;
    private final Inventory inventory = new Inventory();
    private final HashMap<ICommodity, PriceBelief> goodsPriceBelief = new HashMap<>();
    //profit from last round
    private int lookBack = 15;


    public BasicAgent(AgentData data) {
        this.agentName = data.getAgentClassName();
        this.moneyAvailable = data.getMoney();
        this.inventory.fromData(data.getInventory());
        logic = data.getLogic();
        if (data.getLookBack() != null) {
            lookBack = data.getLookBack();
        }
        trackCosts = 0;
    }

    public void simulate(Market market) {
        this.moneyLastRound = this.moneyAvailable;
        logic.perform(this, market);
    }

    public abstract void generateOffers(Market bazaar, ICommodity good);

    public abstract void updatePriceModel(Market bazaar, String act, ICommodity good, boolean success, double unitPrice);

    public abstract Offer createBid(Market bazaar, ICommodity good, double limit);

    public abstract Offer createAsk(Market bazaar, ICommodity commodity_, double limit_);

    public final double queryInventory(ICommodity good) {
        return inventory.query(good);
    }

    public final void produceInventory(ICommodity good, double delta) {
        if (trackCosts < 1)
            trackCosts = 1;

        //FIXME: Not used variable?
        //double curunitcost = inventory.change(good, delta, trackCosts / delta);
        trackCosts = 0;
    }

    public final void consumeInventory(ICommodity good, double delta) {
        if (good.getName().compareTo("money") == 0) {
            this.moneyAvailable += delta;
            if (delta < 0)
                trackCosts += (-delta);

        } else {
            double curunitcost = inventory.change(good, delta, 0);
            if (delta < 0)
                trackCosts += (-delta) * curunitcost;

        }
    }

    public final void changeInventory(ICommodity good, double delta, double unitCost) {
        if (good.getName().compareTo("money") == 0) {
            this.moneyAvailable += delta;
        } else {
            inventory.change(good, delta, unitCost);
        }
    }

    protected double determineSaleQuantity(Market bazaar, ICommodity commodity) {
        double mean = bazaar.getAverageHistoricalPrice(commodity, lookBack);
        //double
        PriceRange trading_range = observeTradingRange(commodity, 10);
        //point
        if (mean > 0) {
            double favorability = trading_range.positionInRange(mean);
            //double
            //position_in_range: high means price is at a high point
            double amount_to_sell = Math.round(favorability * inventory.surplus(commodity));
            //double
            amount_to_sell = inventory.query(commodity);
            if (amount_to_sell < 1) {
                amount_to_sell = 1;
            }

            return amount_to_sell;
        }

        return 0;
    }

    protected double determinePurchaseQuantity(Market bazaar, ICommodity commodity) {
        Double mean = bazaar.getAverageHistoricalPrice(commodity, lookBack);
        //double
        PriceRange tradingRange = observeTradingRange(commodity, 10);
        //Point
        if (tradingRange != null) {
            double favorability = tradingRange.positionInRange(mean);
            //double
            favorability = 1 - favorability;
            //do 1 - favorability to see how close we are to the low end
            double amount_to_buy = Math.round(favorability * inventory.shortage(commodity));
            //double
            if (amount_to_buy < 1) {
                amount_to_buy = 1;
            }

            return amount_to_buy;
        }

        return 0;
    }

    private PriceRange observeTradingRange(ICommodity good, int window) {
        if (!goodsPriceBelief.containsKey(good))
            goodsPriceBelief.put(good, new PriceBelief());

        return goodsPriceBelief.get(good).observe(window);
    }

    public final void updatePriceModel(Market market, String buy, ICommodity good, boolean b) {
        updatePriceModel(market, buy, good, b, 0);
    }

    public AgentSnapshot getSnapshot() {
        return new AgentSnapshot(getAgentName(), getMoneyAvailable(), new Inventory(inventory));
    }

    public boolean isInventoryFull() {
        return inventory.getEmptySpace() == 0;
    }

    public double getProfitFromLastRound() {
        return moneyAvailable - moneyLastRound;
    }

    public String getAgentName() {
        return agentName;
    }

    public double getMoneyAvailable() {
        return moneyAvailable;
    }

    public void setMoneyAvailable(double value) {
        moneyAvailable = value;
    }

    public int getId() {
        return id;
    }

    public double getTrackCosts() {
        return trackCosts;
    }

    public void setTrackCosts(double trackCosts) {
        this.trackCosts = trackCosts;
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public HashMap<ICommodity, PriceBelief> getGoodsPriceBelief() {
        return goodsPriceBelief;
    }

    public int getLookBack() {
        return lookBack;
    }

    public void setLookBack(int lookBack) {
        this.lookBack = lookBack;
    }
}


