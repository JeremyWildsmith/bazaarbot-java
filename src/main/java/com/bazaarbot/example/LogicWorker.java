//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.market.Market;

import java.util.Random;

//make_room_for(agent, "food", 2);
public class LogicWorker  extends Logic
{
    public LogicWorker(Random rnd) {
        super(rnd);
    }

    public LogicWorker() {
    }

    public void perform(BasicAgent agent, Market market) {
        Double food = agent.queryInventory(ExampleCommodity.Food);
        Boolean has_food = food >= 1;
        Double work = agent.queryInventory(ExampleCommodity.Work);
        Boolean need_work = work < 1;
        consume(agent, ExampleCommodity.Food, 1);
        //consume(agent, "money", 0.5);//cost of living/business
        if (need_work)
        {
            produce(agent, ExampleCommodity.Work, 1);
        }
         
    }

}


