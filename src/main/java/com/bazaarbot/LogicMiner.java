//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

//fined $2 for being idle
//_consume(agent, "money", 2);
public class LogicMiner  extends Logic 
{
    public void perform(BasicAgent agent, Market market) throws Exception {
        Double food = agent.queryInventory("food");
        Double tools = agent.queryInventory("tools");
        Double ore = agent.queryInventory("ore");
        Boolean need_ore = ore < 4;
        Boolean has_food = food >= 1;
        Boolean has_tools = tools >= 1;
        //_consume(agent, "money", 0.5);//cost of living/business
        _consume(agent, "food", 1);
        //cost of living
        if (has_food && need_ore)
        {
            if (has_tools)
            {
                //produce 4 ore, consume 1 food, break tools with 10% chance
                _consume(agent, "food", 1);
                _consume(agent,"tools",1,0.1);
                _produce(agent, "ore", 4);
            }
            else
            {
                //produce 2 ore, consume 1 food
                _consume(agent, "food", 1);
                _produce(agent, "ore", 2);
            } 
        }
        else
        {
            //fined $2 for being idle
            //_consume(agent, "money", 2);
            if (!has_food && agent.get_inventoryFull())
            {
            }
             
        } 
    }

}


