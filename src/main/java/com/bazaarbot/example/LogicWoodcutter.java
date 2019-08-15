//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.market.Market;

import java.util.Random;

//make_room_for(agent, "food", 2);
public class LogicWoodcutter  extends Logic
{
    public LogicWoodcutter(Random rnd) {
        super(rnd);
    }

    public LogicWoodcutter() {
    }

    public void perform(BasicAgent agent, Market market) {
        Double food = agent.queryInventory(ExampleCommodity.Food);
        Double tools = agent.queryInventory(ExampleCommodity.Tools);
        Double wood = agent.queryInventory(ExampleCommodity.Wood);
        Boolean need_wood = wood < 4;
        Boolean has_food = food >= 1;
        Boolean has_tools = tools >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        consume(agent, ExampleCommodity.Food, 1);
        //cost of living
        if (has_food && need_wood)
        {
            if (has_tools)
            {
                //produce 2 wood, consume 1 food, break tools with 10% chance
                consume(agent, ExampleCommodity.Food, 1);
                consume(agent,ExampleCommodity.Tools,1,0.1);
                produce(agent, ExampleCommodity.Wood, 2);
            }
            else
            {
                //produce 1 wood, consume 1 food
                consume(agent, ExampleCommodity.Food, 1);
                produce(agent, ExampleCommodity.Wood, 1);
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


