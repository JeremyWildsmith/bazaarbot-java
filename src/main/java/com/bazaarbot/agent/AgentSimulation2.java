package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.Market2;

import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public abstract class AgentSimulation2 {
    private final Random randomGenerator;

    public AgentSimulation2(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }


    protected final void produce(IAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || randomGenerator.nextDouble() < chance) {
            agent.addInventoryItem(commodity, amount);
        }
    }

    protected final void produce(IAgent agent, ICommodity commodity, double amount) {
        produce(agent, commodity, amount, 1.0);
    }

    protected final void consume(IAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || randomGenerator.nextDouble() < chance) {
            agent.consumeInventoryItem(commodity, -amount);
        }
    }

    protected final void consume(IAgent agent, ICommodity commodity, double amount) {
        consume(agent, commodity, amount, 1.0);
    }

    public abstract void simulateActivity(IAgent agent, Market2 market, Statistics statistics);
}
