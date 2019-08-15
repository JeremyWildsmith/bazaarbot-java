//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.market.Market;

import java.util.Random;

//make_room_for(agent, ExampleCommodity.Food, 2); stub todo needed?
public class LogicFarmer  extends Logic
{
    public LogicFarmer(Random rnd) {
        super(rnd);
    }

    public LogicFarmer() {
    }

    public void perform(BasicAgent agent, Market market) {
        Double wood = agent.queryInventory(ExampleCommodity.Wood);
        Double tools = agent.queryInventory(ExampleCommodity.Tools);
        Double food = agent.queryInventory(ExampleCommodity.Food);
        Boolean need_food = food < 10;
        Double work = agent.queryInventory(ExampleCommodity.Work);
        Boolean has_wood = wood >= 1;
        Boolean has_tools = tools >= 1;
        Boolean has_work = work >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        if (need_food)
        {
            if (has_wood && has_tools && has_work)
            {
                //produce 4 food, consume 1 wood, break tools with 10% chance
                consume(agent,ExampleCommodity.Wood,1,1);
                consume(agent,ExampleCommodity.Tools,1,0.1);
                consume(agent,ExampleCommodity.Work,1,1);
                produce(agent,ExampleCommodity.Food,6,1);
            }
            else if (has_wood && !has_tools && has_work)
            {
                //produce 2 food, consume 1 wood
                consume(agent,ExampleCommodity.Wood,1,1);
                consume(agent,ExampleCommodity.Work,1,1);
                produce(agent,ExampleCommodity.Food,3,1);
            }
            else
            {
                //no wood
                //produce 1 food,
                produce(agent,ExampleCommodity.Food,1,1);
            }  
        }
        else
        {
        } 
    }

}


