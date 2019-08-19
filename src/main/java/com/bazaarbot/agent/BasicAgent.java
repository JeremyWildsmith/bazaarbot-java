//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.inventory.Inventory;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BasicAgent {
    //unique integer identifier
    private final String agentName;
    private final AgentSimulation agentSimulation;
    private final Inventory inventory = new Inventory();
    private final List<CommodityPricingHistory> commodityPricingHistories = new ArrayList<>();

    private double moneyAvailable;
    private double moneyLastRound;
    private double moneySpent;

    public BasicAgent(AgentData data) {
        this.agentName = data.getAgentClassName();
        this.moneyAvailable = data.getMoney();
        this.inventory.fromData(data.getInventory());
        this.agentSimulation = data.getAgentSimulation();
    }

    public void simulate(Market market) {
        this.moneyLastRound = this.moneyAvailable;
        agentSimulation.perform(this, market);
    }

    public abstract void generateOffers(Market bazaar, ICommodity good);

    public void updatePriceModel(Market market, String act, ICommodity good, boolean success, double unitPrice) {
        if (success) {
            Optional<CommodityPricingHistory> commodityPricingHistoryOptional = commodityPricingHistories.stream()
                    .filter(commodityPricingHistory -> commodityPricingHistory.getCommodity().equals(good)).findFirst();
            commodityPricingHistoryOptional.ifPresent(commodityPricingHistory -> commodityPricingHistory.addTransaction(unitPrice));
        }
    }

    public abstract Offer createBid(Market bazaar, ICommodity good, double limit);

    public abstract Offer createAsk(Market bazaar, ICommodity commodity, double limit);

    public final double queryInventory(ICommodity good) {
        return inventory.queryAmount(good);
    }

    public final void addInventoryItem(ICommodity good, double amount) {
        commodityPricingHistories.add(new CommodityPricingHistory(good));
        inventory.add(good, amount, (moneySpent >= 1 ? moneySpent : 1) / amount);
    }

    public final void consumeInventoryItem(ICommodity good, double amount) {
        if (good.getName().compareTo("money") == 0) {
            this.moneyAvailable += amount;
            if (amount < 0)
                moneySpent += (-amount);
        } else {
            double price = inventory.change(good, amount, 0);
            if (amount < 0)
                moneySpent += (-amount) * price;
        }
    }

    public final void changeInventory(ICommodity good, double amount, double unitCost) {
        if (good.getName().compareTo("money") == 0) {
            this.moneyAvailable += amount;
        } else {
            inventory.change(good, amount, unitCost);
        }
    }

    protected double determineSaleQuantity(int observeWindow, double averageHistoricalPrice, ICommodity commodity) {
        if (averageHistoricalPrice <= 0) {
            return 0;
        }
        CommodityPricingRange tradingRange = observeTradingRange(commodity, observeWindow);
        double favorability = tradingRange.positionInRange(averageHistoricalPrice);
        //position_in_range: high means price is at a high point
        //TODO: What is going on here?
        double amountToSell = Math.round(favorability * inventory.surplus(commodity));
        //double amount_to_sell = inventory.queryAmount(commodity);
        if (amountToSell < 1) {
            amountToSell = 1;
        }

        return amountToSell;
    }


    protected double determinePurchaseQuantity(int observeWindow, double averageHistoricalPrice, ICommodity commodity) {
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

    private CommodityPricingRange observeTradingRange(ICommodity good, int window) {
        Optional<CommodityPricingHistory> commodityPricingHistoryOptional = commodityPricingHistories.stream()
                .filter(commodityPricingHistory -> commodityPricingHistory.getCommodity().equals(good)).findFirst();
        //TODO: exception needed here
        return commodityPricingHistoryOptional.map(commodityPricingHistory -> commodityPricingHistory.observe(window))
                .orElse(null);
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

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return agentName;
    }
}


