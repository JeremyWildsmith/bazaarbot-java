//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.market.Market;

import java.util.Random;

public abstract class AgentSimulation {
    private final Random rnd;

    public AgentSimulation(Random rnd) {
        this.rnd = rnd;
    }

    public AgentSimulation() {
        this.rnd = new Random();
    }

    /**
     * Perform this logic on the given agent
     *
     * @param agent
     */
    public abstract void perform(BasicAgent agent, Market market);

    protected final void produce(BasicAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || rnd.nextDouble() < chance) {
            agent.addInventoryItem(commodity, amount);
        }
    }

    protected final void produce(BasicAgent agent, ICommodity commodity, double amount) {
        produce(agent, commodity, amount, 1.0);
    }

    protected final void consume(BasicAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || rnd.nextDouble() < chance) {
            agent.consumeInventoryItem(commodity, -amount);
        }
    }

    protected final void consume(BasicAgent agent, ICommodity commodity, double amount) {
        consume(agent, commodity, amount, 1.0);
    }
}


