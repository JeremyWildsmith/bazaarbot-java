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
    public abstract void perform(BasicAgent agent, Market market) throws Exception;

    protected void produce(BasicAgent agent, String commodity, double amount, double chance) throws Exception {
        if (chance >= 1.0 || Quick.rnd.nextDouble() < chance)
        {
            agent.produceInventory(commodity,amount);
        }
         
    }

    protected final void produce(BasicAgent agent, String commodity, double amount) throws Exception {
        produce(agent, commodity, amount, 1.0);
    }

    protected void consume(BasicAgent agent, String commodity, double amount, double chance) throws Exception {
        if (chance >= 1.0 || Quick.rnd.nextDouble() < chance)
        {
            agent.consumeInventory(commodity,-amount);
        }
         
    }

    protected final void consume(BasicAgent agent, String commodity, double amount) throws Exception {
        consume(agent, commodity, amount, 1.0);
    }
}


