//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An agent that performs the basic agentSimulation from the Doran & Parberry article
 *
 * @author
 */
public class DefaultAgent implements IAgent {
    private final ISimulationStrategy simulationStrategy;
    private String agentName;
    private BigDecimal moneyAvailable;
    private double inventoryMaxSize;
    private Map<ICommodity, Double> inventory = new HashMap<>();

    public DefaultAgent(BigDecimal moneyAvailable, double inventoryMaxSize, ISimulationStrategy simulationStrategy) {
        this("DefaultAgent", moneyAvailable, inventoryMaxSize, simulationStrategy);
    }

    public DefaultAgent(String agentName, BigDecimal moneyAvailable, double inventoryMaxSize, ISimulationStrategy simulationStrategy) {
        this.agentName = agentName;
        this.moneyAvailable = moneyAvailable;
        this.inventoryMaxSize = inventoryMaxSize;
        this.simulationStrategy = simulationStrategy;
    }

    @Override
    public final void addCommodity(ICommodity commodity, double amount) {
        if (!checkInventorySpace(amount)) {
            return;
        }
        inventory.put(commodity, amount);
    }

    @Override
    public final double getCommodityAmount(ICommodity commodity) {
        return inventory.containsKey(commodity) ? inventory.get(commodity) : 0;
    }

    @Override
    public List<ICommodity> getAvailableCommodities() {
        return new ArrayList<>(inventory.keySet());
    }

    @Override
    public boolean checkInventorySpace(double amount) {
        if (amount > inventoryMaxSize) {
            return false;
        } else {
            double totalInventorySize = inventory.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
            return !(amount + totalInventorySize > inventoryMaxSize);
        }
    }

    @Override
    public boolean isInventoryFull() {
        return checkInventorySpace(inventoryMaxSize);
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public BigDecimal getMoneyAvailable() {
        return moneyAvailable;
    }

    @Override
    public void setMoneyAvailable(BigDecimal moneyAvailable) {
        this.moneyAvailable = moneyAvailable;
    }

    @Override
    public void simulateActivity(IMarket market, Statistics statistics) {
        this.simulationStrategy.simulateActivity(this, market, statistics);
    }

    @Override
    public String toString() {
        return this.agentName;
    }

}


