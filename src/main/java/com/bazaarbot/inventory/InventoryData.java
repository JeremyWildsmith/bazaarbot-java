//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.inventory;

import com.bazaarbot.ICommodity;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 */
public class InventoryData   
{
    public double maxSize;
    public HashMap<ICommodity,Double> ideal;
    public HashMap<ICommodity,Double> start;

    public InventoryData(double maxSize, HashMap<ICommodity,Double> ideal, HashMap<ICommodity,Double> start) {
        this.maxSize = maxSize;
        this.ideal = ideal;
        this.start = start;
         
    }

}