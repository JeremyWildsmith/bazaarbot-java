package com.bazaarbot.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Inventory
{
    public float maxSize = 0;
    private Map<String, Float> _sizes; // key:commodity_id, val:amount
    private Map<String, Float> _stuff; // ideal counts for each thing
    private Map<String, Float> _ideal; // how much space each thing takes up

    public Inventory()
    {
        _sizes = new HashMap<String, Float>();
        _stuff = new HashMap<String, Float>();
        _ideal = new HashMap<String, Float>();
        maxSize = 0;
    }

    public void fromData(InventoryData data)
    {
        List<String> sizes = new ArrayList<String>();
        List<Float> amounts = new ArrayList<Float>();
        for (String key : data.start.keySet())
        {
            sizes.add(key);
            amounts.add(data.start.get(key));
        }
        setStuff(sizes, amounts);

        sizes = new ArrayList<String>();
        amounts = new ArrayList<Float>();
        for (String key : data.size.keySet())
        {
            sizes.add(key);
            amounts.add(data.size.get(key));
        }

        setSizes(sizes, amounts);
        sizes = new ArrayList<String>();
        amounts = new ArrayList<Float>();

        for (String key : data.ideal.keySet())
        {
            sizes.add(key);
            amounts.add(data.ideal.get(key));
            setIdeal(sizes, amounts);
        }
        maxSize = data.maxSize;
    }

    public Inventory copy()
    {
        Inventory i = new Inventory();
        i._sizes = new HashMap<String, Float>(_sizes);
        i._ideal = new HashMap<String, Float>(_ideal);
        i._stuff = new HashMap<String, Float>(_stuff);
        i.maxSize = maxSize;
        return i;
    }

    /**
     * Set amounts of various commodities
     * @param	stuff
     * @param	amounts
     */

    public void setStuff(List<String> stuff, List<Float> amounts)
    {
        for(int i = 0; i < Math.min(stuff.size(), amounts.size()); i++)
        {
            _stuff.put(stuff.get(i), amounts.get(i));
        }
    }

    /**
     * Set how much of each commodity to stockpile
     * @param	stuff
     * @param	amounts
     */

    public void setIdeal(List<String> stuff, List<Float> amounts)
    {
        for(int i = 0; i < Math.min(stuff.size(), amounts.size()); i++)
        {
            _ideal.put(stuff.get(i), amounts.get(i));
        }
    }

    public void setSizes(List<String> stuff, List<Float> amounts)
    {
        for(int i = 0; i < Math.min(stuff.size(), amounts.size()); i++)
        {
            _sizes.put(stuff.get(i), amounts.get(i));
        }
    }

    /**
     * Returns how much of this
     * @param	good string id of commodity
     * @return
     */

    public float query(String good)
    {
        if (_stuff.containsKey(good))
        {
            return _stuff.get(good);
        }
        return 0;
    }

    public float ideal(String good)
    {
        if (_ideal.containsKey(good))
        {
            return _ideal.get(good);
        }
        return 0;
    }

    public float getEmptySpace()
    {
        return maxSize - getUsedSpace();
    }

    public float getUsedSpace()
    {
        float space_used = 0;
        for (String key : _stuff.keySet())
        {
            space_used += _stuff.get(key) * _sizes.get(key);
        }
        return space_used;
    }

    public float getCapacityFor(String good)
    {
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

    public void change(String good, float delta)
    {
        float result;

        if (_stuff.containsKey(good))
        {
            float amount = _stuff.get(good);
            result = amount + delta;
        }
        else
        {
            result = delta;
        }

        if (result < 0)
        {
            result = 0;
        }

        _stuff.put(good, result);
    }

    /**
     * Returns # of units above the desired inventory level, or 0 if @ or below
     * @param	good string id of commodity
     * @return
     */

    public float surplus(String good)
    {
        float amt = query(good);
        float ideal = _ideal.get(good);
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

    public float shortage(String good)
    {
        if (!_stuff.containsKey(good))
        {
            return 0;
        }
        float amt = query(good);
        float ideal = _ideal.get(good);
        if (amt < ideal)
        {
            return (ideal - amt);
        }
        return 0;
    }

}
