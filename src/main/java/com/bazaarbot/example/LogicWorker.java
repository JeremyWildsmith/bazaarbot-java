//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.Market;

//make_room_for(agent, "food", 2);
public class LogicWorker  extends Logic
{
    public void perform(BasicAgent agent, Market market) throws Exception {
        Double food = agent.queryInventory("food");
        Boolean has_food = food >= 1;
        Double work = agent.queryInventory("work");
        Boolean need_work = work < 1;
        consume(agent, "food", 1);
        //consume(agent, "money", 0.5);//cost of living/business
        if (need_work)
        {
            produce(agent, "work", 1);
        }
         
    }

}


