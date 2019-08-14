//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory   
{
    public double maxSize = 0;

    // En-route items, expecting via contract
    // Key: commodity_it, val: amount, original_cost
    private HashMap<String,Point> _expecting;

    // key:commodity_id, val:amount, original_cost
    private HashMap<String,Point> _stuff;

    // ideal counts for each thing
    private HashMap<String,Double> _ideal;

    // how much space each thing takes up
    private HashMap<String,Double> _sizes;

    public Inventory() {
        _sizes = new HashMap<String,Double>();
        _stuff = new HashMap<String,Point>();
        _ideal = new HashMap<String,Double>();
        _expecting = new HashMap<String,Point>();
        maxSize = 0;
    }

    public void fromData(InventoryData data) {
        List<String> sizes = new ArrayList<String>();
        List<Point> amountsp = new ArrayList<Point>();
        for (String key : data.start.keySet())
        {
            sizes.add(key);
            amountsp.add(new Point(data.start.get(key),0));
        }

        for (int i = 0;i < sizes.size();i++)
        {
            _stuff.put(sizes.get(i), amountsp.get(i));
        }

        sizes = new ArrayList<>();
        List<Double> amounts = new ArrayList<Double>();
        for (String key : data.size.keySet())
        {
            sizes.add(key);
            amounts.add(data.size.get(key));
        }

        for (int i = 0;i < sizes.size();i++)
        {
            _sizes.put(sizes.get(i), amounts.get(i));
        }

        sizes = new ArrayList<>();
        amounts = new ArrayList<>();
        for (String key : data.ideal.keySet())
        {
            sizes.add(key);
            amounts.add(data.ideal.get(key));

            for (int i = 0;i < sizes.size();i++)
            {
                _ideal.put(sizes.get(i), amounts.get(i));
            }
        }
        maxSize = data.maxSize;
    }

    /**
     * Returns how much of this
     * @param	good string id of commodity
     * @return
     */
    public double query(String good) {
        if (_stuff.containsKey(good))
        {
            return _stuff.get(good).x;
        }

        return 0;
    }

    /**
     * Returns how much of this expected
     * @param	good string id of commodity
     * @return
     */
    public double queryExpecting(String good) {
        if (_expecting.containsKey(good))
        {
            return _expecting.get(good).x;
        }

        return 0;
    }

    public double query_cost(String good) {
        if (_stuff.containsKey(good))
        {
            return _stuff.get(good).y;
        }
         
        return 0;
    }

    public double getEmptySpace() {
        return maxSize - getUsedSpace();
    }

    public double getUsedSpace() {
        double space_used = 0;
        for (String key : _stuff.keySet())
        {
            if (!_sizes.containsKey(key))
                continue;
             
            space_used += _stuff.get(key).x * _sizes.get(key);
        }
        return space_used;
    }

    public double getCapacityFor(String good) {
        if (_sizes.containsKey(good))
        {
            return _sizes.get(good);
        }
         
        return -1;
    }

    /**
     * Change the amount of the given commodity by delta
     * @param	good string id of commodity
     * @param	delta amount added
     */
    public double change(String good, double delta, double unit_cost) {
        Point result = new Point(0,0);
        if (_stuff.containsKey(good))
        {
            Point amount = _stuff.get(good);
            if (unit_cost > 0)
            {
                //If we did not have any previous inventory for this item
                if (amount.x <= 0)
                {
                    result.x = delta;
                    result.y = unit_cost;
                }
                else
                {
                    //original_price = Average the two costs
                    result.y = (amount.x * amount.y + delta * unit_cost) / (amount.x + delta);
                    result.x = amount.x + delta;
                } 
            }
            else
            {
                result.x = amount.x + delta;
                result.y = amount.y;
            } 
        }
        else
        {
            //just copy from old value?
            result.x = delta;
            result.y = unit_cost;
        } 
        if (result.x < 0)
        {
            result.x = 0;
            result.y = 0;
        }
         
        _stuff.put(good, result);
        return result.y;
    }

    /**
     * Change the amount of the expected commodity by delta
     * @param	good string id of commodity
     * @param	delta amount added
     */
    public double changeExpecting(String good, double delta, double unit_cost) {
        Point result = new Point(0,0);
        if (_stuff.containsKey(good))
        {
            Point amount = _expecting.get(good);
            if (unit_cost > 0)
            {
                //If we did not have any previous inventory for this item
                if (amount.x <= 0)
                {
                    result.x = delta;
                    result.y = unit_cost;
                }
                else
                {
                    //original_price = Average the two costs
                    result.y = (amount.x * amount.y + delta * unit_cost) / (amount.x + delta);
                    result.x = amount.x + delta;
                }
            }
            else
            {
                result.x = amount.x + delta;
                result.y = amount.y;
            }
        }
        else
        {
            //just copy from old value?
            result.x = delta;
            result.y = unit_cost;
        }
        if (result.x < 0)
        {
            result.x = 0;
            result.y = 0;
        }

        _expecting.put(good, result);
        return result.y;
    }

    //return current unit cost
    /**
     * Returns # of units above the desired inventory level, or 0 if @ or below
     * @param	good string id of commodity
     * @return
     */
    public double surplus(String good) {
        Double amt = query(good);
        double ideal = 0;
        if (_ideal.containsKey(good))
            ideal = _ideal.get(good);
         
        if (amt > ideal)
        {
            return (amt - ideal);
        }
         
        return 0;
    }

    /**
     * Returns # of units below the desired inventory level, or 0 if @ or above
     * @param	good
     * @return
     */
    public double shortage(String good) {
        if (!_stuff.containsKey(good))
        {
            return 0;
        }
         
        Double amt = query(good) + queryExpecting(good);

        double ideal = 0;
        if (_ideal.containsKey(good))
            ideal = _ideal.get(good);
         
        if (amt < ideal)
        {
            return (ideal - amt);
        }
         
        return 0;
    }

}


