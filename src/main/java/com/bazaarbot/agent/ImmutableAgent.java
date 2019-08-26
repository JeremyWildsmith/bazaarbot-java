package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Gritsenko
 */
public final class ImmutableAgent implements IAgent {
    private final String agentName;
    private final BigDecimal moneyAvailable;
    private final Map<ICommodity, Double> stuff = new HashMap<>();

    public ImmutableAgent(IAgent agent) {
        this.agentName = agent.getAgentName();
        this.moneyAvailable = agent.getMoneyAvailable();
        for (ICommodity commodity : agent.getAvailableCommodities()) {
            this.stuff.put(commodity, agent.getCommodityAmount(commodity));
        }
    }

    @Override
    public void addCommodity(ICommodity good, double amount) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public double getCommodityAmount(ICommodity commodity) {
        return stuff.get(commodity);
    }

    @Override
    public boolean isInventoryFull() {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public boolean checkInventorySpace(double amount) {
        throw new RuntimeException("Not implemented!");
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
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public void simulateActivity(IMarket market, Statistics statistics) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public List<ICommodity> getAvailableCommodities() {
        return List.copyOf(stuff.keySet());
    }

    @Override
    public String toString() {
        return agentName + "-Immutable";
    }
}
