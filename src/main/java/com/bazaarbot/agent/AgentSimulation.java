//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.market.Market;

import java.util.Random;

public abstract class AgentSimulation {
    private final Random rnd;
    private final String name;

    public AgentSimulation(String name, Random rnd) {
        this.name = name;
        this.rnd = rnd;
    }

    public AgentSimulation(String name) {
        this(name, new Random());
    }

    /**
     * Perform this logic on the given agent
     *
     * @param agent
     */
    public abstract void perform(IAgent agent, Market market);

    protected final void produce(IAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || rnd.nextDouble() < chance) {
            agent.addInventoryItem(commodity, amount);
        }
    }

    protected final void produce(IAgent agent, ICommodity commodity, double amount) {
        produce(agent, commodity, amount, 1.0);
    }

    protected final void consume(IAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || rnd.nextDouble() < chance) {
            agent.consumeInventoryItem(commodity, -amount);
        }
    }

    protected final void consume(IAgent agent, ICommodity commodity, double amount) {
        consume(agent, commodity, amount, 1.0);
    }

    public String getName() {
        return name;
    }
}


