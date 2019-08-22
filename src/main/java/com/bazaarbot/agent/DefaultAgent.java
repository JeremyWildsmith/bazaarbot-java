//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.inventory.Inventory;
import com.bazaarbot.inventory.InventoryData;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Market2;
import com.bazaarbot.market.Offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An agent that performs the basic agentSimulation from the Doran & Parberry article
 *
 * @author
 */
public class DefaultAgent implements IAgent {
    //A small scaling of the ask price (to generate profit)
    private static final double ASK_PRICE_INFLATION = 1.02;
    private static final int DEFAULT_LOOKBACK = 15;
    private static final int OBSERVE_WINDOW = 10;

    private String agentName;
    private double moneyAvailable;
    private double moneyLastSimulation;
    private double moneySpent;
    private Inventory inventory;
    private List<CommodityPricingHistory> commodityPricingHistories = new ArrayList<>();


    public DefaultAgent(String agentName, InventoryData data, double moneyAvailable) {
        this.agentName = agentName;
        this.moneyAvailable = moneyAvailable;
        this.inventory = new Inventory();
        this.inventory.fromData(data);
        this.moneySpent = 0;
    }

    private double determineSaleQuantity(int observeWindow, double averageHistoricalPrice, ICommodity commodity) {
        if (averageHistoricalPrice <= 0) {
            return 0;
        }
        CommodityPricingRange tradingRange = observeTradingRange(commodity, observeWindow);
        double favorability = tradingRange == null ? 1 : tradingRange.positionInRange(averageHistoricalPrice);
        //position_in_range: high means price is at a high point
        //TODO: What is going on here?
        double amountToSell = Math.round(favorability * inventory.surplus(commodity));
        //double amount_to_sell = inventory.queryAmount(commodity);
        if (amountToSell < 1) {
            amountToSell = 1;
        }

        return amountToSell;
    }

    private double determinePurchaseQuantity(int observeWindow, double averageHistoricalPrice, ICommodity commodity) {
        if (averageHistoricalPrice <= 0) {
            return 0;
        }
        CommodityPricingRange tradingRange = observeTradingRange(commodity, observeWindow);
        double favorability = tradingRange == null ? 1 : tradingRange.positionInRange(averageHistoricalPrice);
        //double
        favorability = 1 - favorability;
        //do 1 - favorability to see how close we are to the low end
        double amountToBuy = Math.round(favorability * inventory.shortage(commodity));
        //double
        if (amountToBuy < 1) {
            amountToBuy = 1;
        }

        return amountToBuy;

    }

    private CommodityPricingRange observeTradingRange(ICommodity commodity, int window) {
        Optional<CommodityPricingHistory> commodityPricingHistoryOptional = commodityPricingHistories.stream()
                .filter(commodityPricingHistory -> commodityPricingHistory.getCommodity().equals(commodity)).findFirst();
        //TODO: exception needed here
        return commodityPricingHistoryOptional.map(commodityPricingHistory -> commodityPricingHistory.observe(window))
                .orElse(null);
    }

    @Override
    public void simulate(Market market) {
        moneyLastSimulation = moneyAvailable;
    }

    @Override
    public void simulate(Market2 market) {
        moneyLastSimulation = moneyAvailable;
    }

    @Override
    public Offer createBid(Market market, ICommodity commodity, double limit) {
        // determinePriceOf(good);  bids are now made "at market", no price determination needed
        double ideal = determinePurchaseQuantity(OBSERVE_WINDOW, market.getAverageHistoricalPrice(commodity, DEFAULT_LOOKBACK), commodity);
        //can't buy more than limit
        double quantityToBuy = ideal > limit ? limit : ideal;
        double bidPrice = inventory.queryCost(commodity) * ASK_PRICE_INFLATION;
        if (quantityToBuy > 0) {
            return new Offer(this, commodity, quantityToBuy, bidPrice);
        }

        return null;
    }

    @Override
    public Offer createAsk(Market market, ICommodity commodity, double limit) {
        double ideal = determineSaleQuantity(OBSERVE_WINDOW, market.getAverageHistoricalPrice(commodity, DEFAULT_LOOKBACK), commodity);
        //asks are fair prices:  costs + small profit
        double quantityToSell = ideal > limit ? limit : ideal;//inventory.queryAmount(commodity);

        //put asks out for all inventory
        double askPrice = inventory.queryCost(commodity) * ASK_PRICE_INFLATION;

        return new Offer(this, commodity, quantityToSell, askPrice);
    }

    @Override
    public void generateOffers(Market market, ICommodity commodity) {
        Offer offer;
        double surplus = inventory.surplus(commodity);
        if (surplus >= 1) {
            offer = createAsk(market, commodity, 1);
            market.ask(offer);
        } else {
            double shortage = inventory.shortage(commodity);
            double space = inventory.getEmptySpace();
            if (shortage > 0 && space > 0) {
                if (shortage <= space) {
                    //enough space for ideal order
                    offer = createBid(market, commodity, shortage);
                } else {
                    //not enough space for ideal order
                    offer = createBid(market, commodity, space);
                }
                market.bid(offer);
            }
        }
    }

    @Override
    public void updatePriceModel(String act, ICommodity commodity, boolean success, double unitPrice) {
        if (success) {
            Optional<CommodityPricingHistory> commodityPricingHistoryOptional = commodityPricingHistories.stream()
                    .filter(commodityPricingHistory -> commodityPricingHistory.getCommodity().equals(commodity)).findFirst();
            commodityPricingHistoryOptional.ifPresent(commodityPricingHistory -> commodityPricingHistory.addTransaction(unitPrice));
        }
    }

    @Override
    public final void addInventoryItem(ICommodity good, double amount) {
        commodityPricingHistories.add(new CommodityPricingHistory(good));
        inventory.add(good, amount, (moneySpent >= 1 ? moneySpent : 1) / amount);
    }

    @Override
    public final double queryInventory(ICommodity commodity) {
        return inventory.queryAmount(commodity);
    }

    @Override
    public final void produceInventory(ICommodity good, double delta) {
        if (moneySpent < 1)
            moneySpent = 1;

        double curunitcost = inventory.change(good, delta, moneySpent / delta);
        moneySpent = 0;
    }

    @Override
    public final void consumeInventoryItem(ICommodity commodity, double amount) {
        if (commodity.getName().compareTo("moneyAvailable") == 0) {
            this.moneyAvailable += amount;
            if (amount < 0)
                moneySpent += (-amount);
        } else {
            double price = inventory.change(commodity, amount, 0);
            if (amount < 0)
                moneySpent += (-amount) * price;
        }
    }

    @Override
    public final void changeInventory(ICommodity commodity, double amount, double unitCost) {
        if (commodity.getName().compareTo("money") == 0) {
            this.moneyAvailable += amount;
        } else {
            inventory.change(commodity, amount, unitCost);
        }
    }

    @Override
    public AgentSnapshot getSnapshot() {
        return new AgentSnapshot(getAgentName(), getMoneyAvailable(), new Inventory(inventory));
    }

    @Override
    public boolean isInventoryFull() {
        return inventory.getEmptySpace() == 0;
    }

    @Override
    public double getLastSimulateProfit() {
        return getMoneyAvailable() - moneyLastSimulation;
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public double getMoneyAvailable() {
        return moneyAvailable;
    }

    @Override
    public void setMoneyAvailable(double value) {
        moneyAvailable = value;
    }

    @Override
    public String toString() {
        return this.agentName;
    }




}


