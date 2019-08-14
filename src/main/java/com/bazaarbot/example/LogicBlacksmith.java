//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.Market;

//}
public class LogicBlacksmith  extends Logic
{
    public void perform(BasicAgent agent, Market market) {
        Double food = agent.queryInventory(ExampleCommodity.Food);
        Double metal = agent.queryInventory(ExampleCommodity.Metal);
        Double tools = agent.queryInventory(ExampleCommodity.Tools);
        Boolean need_tools = tools < 4;
        Boolean has_food = food >= 1;
        Boolean has_metal = metal >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        consume(agent, ExampleCommodity.Food, 1);
        //cost of living
        if (has_food && has_metal & need_tools)
        {
            //convert all metal into tools
            consume(agent, ExampleCommodity.Metal, metal);
            produce(agent, ExampleCommodity.Tools, metal);
        }
        else
        {
            //fined $2 for being idle
            //consume(agent, "money", 2);
            if (!has_food && agent.isInventoryFull())
            {
            }
             
        } 
    }

}


