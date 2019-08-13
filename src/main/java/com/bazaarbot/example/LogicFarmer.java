//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.Market;

//make_room_for(agent, "food", 2); stub todo needed?
public class LogicFarmer  extends Logic
{
    public void perform(BasicAgent agent, Market market) throws Exception {
        Double wood = agent.queryInventory("wood");
        Double tools = agent.queryInventory("tools");
        Double food = agent.queryInventory("food");
        Boolean need_food = food < 10;
        Double work = agent.queryInventory("work");
        Boolean has_wood = wood >= 1;
        Boolean has_tools = tools >= 1;
        Boolean has_work = work >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        if (need_food)
        {
            if (has_wood && has_tools && has_work)
            {
                //produce 4 food, consume 1 wood, break tools with 10% chance
                consume(agent,"wood",1,1);
                consume(agent,"tools",1,0.1);
                consume(agent,"work",1,1);
                produce(agent,"food",6,1);
            }
            else if (has_wood && !has_tools && has_work)
            {
                //produce 2 food, consume 1 wood
                consume(agent,"wood",1,1);
                consume(agent,"work",1,1);
                produce(agent,"food",3,1);
            }
            else
            {
                //no wood
                //produce 1 food,
                produce(agent,"food",1,1);
            }  
        }
        else
        {
        } 
    }

}


