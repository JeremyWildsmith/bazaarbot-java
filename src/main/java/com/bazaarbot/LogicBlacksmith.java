//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

//}
public class LogicBlacksmith  extends Logic 
{
    public void perform(BasicAgent agent, Market market) throws Exception {
        Double food = agent.queryInventory("food");
        Double metal = agent.queryInventory("metal");
        Double tools = agent.queryInventory("tools");
        Boolean need_tools = tools < 4;
        Boolean has_food = food >= 1;
        Boolean has_metal = metal >= 1;
        //_consume(agent, "money", 0.5);//cost of living/business
        _consume(agent, "food", 1);
        //cost of living
        if (has_food && has_metal & need_tools)
        {
            //convert all metal into tools
            _consume(agent, "metal", metal);
            _produce(agent, "tools", metal);
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


