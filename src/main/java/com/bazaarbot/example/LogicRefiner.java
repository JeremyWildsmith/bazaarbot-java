//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.market.Market;

import java.util.Random;

//make_room_for(agent,"food",2);
public class LogicRefiner  extends Logic
{
    public LogicRefiner(Random rnd) {
        super(rnd);
    }

    public LogicRefiner() {
    }

    public void perform(BasicAgent agent, Market market) {
        Double food = agent.queryInventory(ExampleCommodity.Food);
        Double tools = agent.queryInventory(ExampleCommodity.Tools);
        Double ore = agent.queryInventory(ExampleCommodity.Ore);
        if (ore > 4)
            ore = 4.0;
         
        Double metal = agent.queryInventory(ExampleCommodity.Metal);
        Boolean need_metal = metal < 4;
        Boolean has_food = food >= 1;
        Boolean has_tools = tools >= 1;
        Boolean has_ore = ore >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        consume(agent, ExampleCommodity.Food, 1);
        //cost of living
        if (has_food && has_ore && need_metal)
        {
            if (has_tools)
            {
                //convert all ore into metal, consume 1 food, break tools with 10% chance
                consume(agent, ExampleCommodity.Ore, ore);
                consume(agent, ExampleCommodity.Food, 1);
                consume(agent,ExampleCommodity.Tools,1,0.1);
                produce(agent, ExampleCommodity.Metal, ore);
            }
            else
            {
                //convert up to 2 ore into metal, consume 1 food
                Double max = agent.queryInventory(ExampleCommodity.Ore);
                if (max > 2)
                {
                    max = 2.0;
                }
                 
                consume(agent, ExampleCommodity.Ore, max);
                consume(agent, ExampleCommodity.Food, 1);
                produce(agent, ExampleCommodity.Metal, max);
            } 
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


