//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryLog   
{
    EconNoun type = EconNoun.Price;
    HashMap<String,ArrayList<Double>> log;
    public HistoryLog(EconNoun type) {
        this.type = type;
        log = new HashMap<String, ArrayList<Double>>();
    }

    /**
    	     * Add a new entry to this log
    	     * @param	name
    	     * @param	amount
    	     */
    public void add(String name, double amount) {
        if (log.containsKey(name))
        {
            List<Double> list = log.get(name);
            list.add(amount);
        }
         
    }

    /**
    	     * Register a new category list in this log
    	     * @param	name
    	     */
    public void register(String name) {
        if (!log.containsKey(name))
        {
            log.put(name, new ArrayList<Double>());
        }
         
    }

    /**
    	     * Returns the average amount of the given category, looking backwards over a specified range
    	     * @param	name the category of thing
    	     * @param	range how far to look back
    	     * @return
    	     */
    public double average(String name, int range) {
        if (log.containsKey(name))
        {
            List<Double> list = log.get(name);
            double amt = 0.0;
            int length = list.size();
            if (length < range)
            {
                range = length;
            }
             
            for (int i = 0;i < range;i++)
            {
                amt += list.get(length - 1 - i);
            }
            if (range <= 0)
                return -1;
             
            return amt / range;
        }
         
        return 0;
    }

}


