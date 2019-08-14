//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.BasicAgent;
import com.bazaarbot.Logic;
import com.bazaarbot.Market;

//make_room_for(agent,"food",2);
public class LogicRefiner  extends Logic
{
    public void perform(BasicAgent agent, Market market) {
        Double food = agent.queryInventory("food");
        Double tools = agent.queryInventory("tools");
        Double ore = agent.queryInventory("ore");
        if (ore > 4)
            ore = 4.0;
         
        Double metal = agent.queryInventory("metal");
        Boolean need_metal = metal < 4;
        Boolean has_food = food >= 1;
        Boolean has_tools = tools >= 1;
        Boolean has_ore = ore >= 1;
        //consume(agent, "money", 0.5);//cost of living/business
        consume(agent, "food", 1);
        //cost of living
        if (has_food && has_ore && need_metal)
        {
            if (has_tools)
            {
                //convert all ore into metal, consume 1 food, break tools with 10% chance
                consume(agent, "ore", ore);
                consume(agent, "food", 1);
                consume(agent,"tools",1,0.1);
                produce(agent, "metal", ore);
            }
            else
            {
                //convert up to 2 ore into metal, consume 1 food
                Double max = agent.queryInventory("ore");
                if (max > 2)
                {
                    max = 2.0;
                }
                 
                consume(agent, "ore", max);
                consume(agent, "food", 1);
                produce(agent, "metal", max);
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


