package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;

import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public abstract class AgentSimulation2 {
    protected final IAgent agent;
    private final Random randomGenerator;

    public AgentSimulation2(IAgent agent, Random randomGenerator) {
        this.agent = agent;
        this.randomGenerator = randomGenerator;
    }


    public abstract void simulateActivity();

    protected final void produce(ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || randomGenerator.nextDouble() < chance) {
            agent.addInventoryItem(commodity, amount);
        }
    }

    protected final void produce(ICommodity commodity, double amount) {
        produce(commodity, amount, 1.0);
    }

    protected final void consume(ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || randomGenerator.nextDouble() < chance) {
            agent.consumeInventoryItem(commodity, -amount);
        }
    }

    protected final void consume(ICommodity commodity, double amount) {
        consume(commodity, amount, 1.0);
    }

}
