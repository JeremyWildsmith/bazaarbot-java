//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.market.Market;

import java.util.Random;

//fined $2 for being idle
//consume(agent, "money", 2);
public class LogicMiner  extends Logic
{
    public LogicMiner(Random rnd) {
        super(rnd);
    }

    public LogicMiner() {
    }

    public void perform(BasicAgent agent, Market market) {
        Double food = agent.queryInventory(ExampleCommodity.Food);
        Double tools = agent.queryInventory(ExampleCommodity.Tools);
        Double ore = agent.queryInventory(ExampleCommodity.Ore);
        Boolean need_ore = ore < 4;
        Boolean has_food = food >= 1;
        Boolean has_tools = tools >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        consume(agent, ExampleCommodity.Food, 1);
        //cost of living
        if (has_food && need_ore)
        {
            if (has_tools)
            {
                //produce 4 ore, consume 1 food, break tools with 10% chance
                consume(agent, ExampleCommodity.Food, 1);
                consume(agent,ExampleCommodity.Tools,1,0.1);
                produce(agent, ExampleCommodity.Ore, 4);
            }
            else
            {
                //produce 2 ore, consume 1 food
                consume(agent, ExampleCommodity.Food, 1);
                produce(agent, ExampleCommodity.Ore, 2);
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


