//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.inventory;


import com.bazaarbot.ICommodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory   
{
    public double maxSize = 0;

    private HashMap<ICommodity, InventoryEntry> _expecting;
    private HashMap<ICommodity, InventoryEntry> _stuff;
    private HashMap<ICommodity, Double> _ideal;

    public Inventory() {
        _stuff = new HashMap<>();
        _ideal = new HashMap<>();
        _expecting = new HashMap<>();
        maxSize = 0;
    }

    public Inventory(Inventory src) {
        _stuff = new HashMap<>(src._stuff);
        _ideal = new HashMap<>(src._ideal);
        _expecting = new HashMap<>(src._expecting);
        maxSize = src.maxSize;
    }

    public void fromData(InventoryData data) {
        List<ICommodity> sizes = new ArrayList<>();
        List<InventoryEntry> amountsp = new ArrayList<InventoryEntry>();
        for (ICommodity key : data.start.keySet())
        {
            sizes.add(key);
            amountsp.add(new InventoryEntry(data.start.get(key),0));
        }

        for (int i = 0;i < sizes.size();i++)
        {
            _stuff.put(sizes.get(i), amountsp.get(i));
        }


        sizes = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        for (ICommodity key : data.ideal.keySet())
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
    public double query(ICommodity good) {
        if (_stuff.containsKey(good))
        {
            return _stuff.get(good).amount;
        }

        return 0;
    }

    /**
     * Returns how much of this expected
     * @param	good string id of commodity
     * @return
     */
    public double queryExpecting(ICommodity good) {
        if (_expecting.containsKey(good))
        {
            return _expecting.get(good).amount;
        }

        return 0;
    }

    public double query_cost(ICommodity good) {
        if (_stuff.containsKey(good))
        {
            return _stuff.get(good).origialPrice;
        }
         
        return 0;
    }

    public double getEmptySpace() {
        return maxSize - getUsedSpace();
    }

    public double getUsedSpace() {
        double space_used = 0;
        for (ICommodity key : _stuff.keySet())
        {
            space_used += _stuff.get(key).amount * key.getSpace();
        }
        return space_used;
    }

    /**
     * Change the amount of the given commodity by delta
     * @param	good string id of commodity
     * @param	delta amount added
     */
    public double change(ICommodity good, double delta, double unit_cost) {
        double resultAmount = 0;
        double resultPrice = 0;

        if (_stuff.containsKey(good))
        {
            InventoryEntry current = _stuff.get(good);
            if (unit_cost > 0)
            {
                //If we did not have any previous inventory for this item
                if (current.amount <= 0)
                {
                    resultAmount = delta;
                    resultPrice = unit_cost;
                }
                else
                {
                    //original_price = Average the two costs
                    resultPrice = (current.amount * current.origialPrice + delta * unit_cost) / (current.amount + delta);
                    resultAmount= current.amount + delta;
                } 
            }
            else
            {
                resultAmount = current.amount + delta;
                resultPrice = current.origialPrice;
            } 
        }
        else
        {
            //just copy from old value?
            resultAmount = delta;
            resultPrice = unit_cost;
        } 
        if (resultAmount < 0)
        {
            resultAmount = 0;
            resultPrice = 0;
        }
         
        _stuff.put(good, new InventoryEntry(resultAmount, resultPrice));
        return resultPrice;
    }

    /**
     * Change the amount of the expected commodity by delta
     * @param	good string id of commodity
     * @param	delta amount added
     */
    public double changeExpecting(ICommodity good, double delta, double unit_cost) {
        double resultAmount = 0;
        double resultPrice = 0;

        if (_expecting.containsKey(good))
        {
            InventoryEntry current = _expecting.get(good);
            if (unit_cost > 0)
            {
                //If we did not have any previous inventory for this item
                if (current.amount <= 0)
                {
                    resultAmount = delta;
                    resultPrice = unit_cost;
                }
                else
                {
                    //original_price = Average the two costs
                    resultPrice = (current.amount * current.origialPrice + delta * unit_cost) / (current.amount + delta);
                    resultAmount= current.amount + delta;
                }
            }
            else
            {
                resultAmount = current.amount + delta;
                resultPrice = current.origialPrice;
            }
        }
        else
        {
            //just copy from old value?
            resultAmount = delta;
            resultPrice = unit_cost;
        }
        if (resultAmount < 0)
        {
            resultAmount = 0;
            resultPrice = 0;
        }

        _expecting.put(good, new InventoryEntry(resultAmount, resultPrice));
        return resultPrice;
    }

    //return current unit cost
    /**
     * Returns # of units above the desired inventory level, or 0 if @ or below
     * @param	good string id of commodity
     * @return
     */
    public double surplus(ICommodity good) {
        double amt = query(good);
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
    public double shortage(ICommodity good) {
        if (!_stuff.containsKey(good))
        {
            return 0;
        }
         
        double amt = query(good) + queryExpecting(good);

        double ideal = 0;
        if (_ideal.containsKey(good))
            ideal = _ideal.get(good);
         
        if (amt < ideal)
        {
            return (ideal - amt);
        }
         
        return 0;
    }

    private static final class InventoryEntry {
        final double amount;
        final double origialPrice;

        InventoryEntry(double amount, double origialPrice) {
            this.amount = amount;
            this.origialPrice = origialPrice;
        }
    }
}


