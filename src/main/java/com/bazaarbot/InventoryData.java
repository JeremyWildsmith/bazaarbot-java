//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 */
public class InventoryData   
{
    public double maxSize;
    public HashMap<String,Double> ideal;
    public HashMap<String,Double> start;
    public HashMap<String,Double> size;
    public InventoryData(double maxSize, HashMap<String,Double> ideal, HashMap<String,Double> start, HashMap<String,Double> size) throws Exception {
        this.maxSize = maxSize;
        this.ideal = ideal;
        this.start = start;
        this.size = size;
        if (this.size == null)
        {
            this.size = new HashMap<String,Double>();
            for (Object __dummyForeachVar0 : start.entrySet())
            {
                Entry<String,Double> entry = (Entry<String,Double>)__dummyForeachVar0;
                this.size.put(entry.getKey(), 1.0);
            }
        }
         
    }

    public InventoryData(String data) throws Exception {
    }

}


//var maxSize:Int = data.max_size;
//var ideal = new Map<String, Float>();
//var start = new Map<String, Float>();
//var size  = new Map<String, Float>();
//var startArray = Reflect.fields(data.start);
//if (startArray != null)
//{
//    for (s in startArray)
//    {
//        start.set(s, cast Reflect.field(data.start, s));
//        size.set(s, 1);	//initialize size of every item to 1 by default
//    }
//}
//var idealArray = Reflect.fields(data.ideal);
//if (idealArray != null)
//{
//    for (i in idealArray)
//    {
//        ideal.set(i, cast Reflect.field(data.ideal, i));
//    }
//}
//return new InventoryData(maxSize, ideal, start, size);