//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

public abstract class Logic
{
    /**
     * Perform this logic on the given agent
     * @param	agent
     */
    public abstract void perform(BasicAgent agent, Market market);

    protected void produce(BasicAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || Quick.rnd.nextDouble() < chance)
        {
            agent.produceInventory(commodity,amount);
        }
         
    }

    protected final void produce(BasicAgent agent, ICommodity commodity, double amount) {
        produce(agent, commodity, amount, 1.0);
    }

    protected void consume(BasicAgent agent, ICommodity commodity, double amount, double chance) {
        if (chance >= 1.0 || Quick.rnd.nextDouble() < chance)
        {
            agent.consumeInventory(commodity,-amount);
        }
         
    }

    protected final void consume(BasicAgent agent, ICommodity commodity, double amount) {
        consume(agent, commodity, amount, 1.0);
    }
}


