//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

public class Logic
{
    private boolean init = false;
    //public function new(?data:Dynamic)
    //{
    //    //no implemenation -- provide your own in a subclass
    //}
    /**
    	     * Perform this logic on the given agent
    	     * @param	agent
    	     */
    public void perform(BasicAgent agent, Market market) throws Exception {
    }

    //no implemenation -- provide your own in a subclass
    protected void _produce(BasicAgent agent, String commodity, double amount, double chance) throws Exception {
        if (chance >= 1.0 || Quick.rnd.nextDouble() < chance)
        {
            agent.produceInventory(commodity,amount);
        }
         
    }

    protected final void _produce(BasicAgent agent, String commodity, double amount) throws Exception {
        _produce(agent, commodity, amount, 1.0);
    }

    protected void _consume(BasicAgent agent, String commodity, double amount, double chance) throws Exception {
        if (chance >= 1.0 || Quick.rnd.nextDouble() < chance)
        {
            //if (commodity == "money")
            //{
            //    agent.changeInventory(comm
            //    agent.money -= amount;
            //}
            //else
            //{
            agent.consumeInventory(commodity,-amount);
        }
         
    }

    protected final void _consume(BasicAgent agent, String commodity, double amount) throws Exception {
        _consume(agent, commodity, amount, 1.0);
    }

}


