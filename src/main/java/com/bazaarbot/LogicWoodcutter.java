//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

//make_room_for(agent, "food", 2);
public class LogicWoodcutter  extends Logic 
{
    public void perform(BasicAgent agent, Market market) throws Exception {
        Double food = agent.queryInventory("food");
        Double tools = agent.queryInventory("tools");
        Double wood = agent.queryInventory("wood");
        Boolean need_wood = wood < 4;
        Boolean has_food = food >= 1;
        Boolean has_tools = tools >= 1;
        //_consume(agent, "money", 0.5);//cost of living/business
        _consume(agent, "food", 1);
        //cost of living
        if (has_food && need_wood)
        {
            if (has_tools)
            {
                //produce 2 wood, consume 1 food, break tools with 10% chance
                _consume(agent, "food", 1);
                _consume(agent,"tools",1,0.1);
                _produce(agent, "wood", 2);
            }
            else
            {
                //produce 1 wood, consume 1 food
                _consume(agent, "food", 1);
                _produce(agent, "wood", 1);
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


