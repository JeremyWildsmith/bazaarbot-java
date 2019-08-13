package com.bazaarbot.agent;

import java.util.Map;

class Logic
{
    private boolean init = false;

    public Logic(Map<String, Object> data)
    {
        //no implemenation -- provide your own in a subclass
    }

    /**
     * Perform this logic on the given agent
     * @param	agent
     */

    public void perform(BasicAgent agent, Market market)
    {
        //no implemenation -- provide your own in a subclass
    }

    protected void _produce(BasicAgent agent, String commodity, float amount) {
        _produce(agent, commodity, amount, 1.0f);
    }

    protected void _produce(BasicAgent agent, String commodity, float amount, float chance)
    {
        if (chance >= 1.0 || Math.random() < chance)
        {
            agent.changeInventory(commodity, amount);
        }
    }

    protected void _consume(BasicAgent agent, String commodity, float amount) {
        _consume(agent, commodity, amount, 1.0f);
    }

    protected void _consume(BasicAgent agent, String commodity, float amount, float chance)
    {
        if (chance >= 1.0 || Math.random() < chance)
        {
            if (commodity == "money")
            {
                agent.money -= amount;
            }
            else
            {
                agent.changeInventory(commodity, -amount);
            }
        }
    }

}
