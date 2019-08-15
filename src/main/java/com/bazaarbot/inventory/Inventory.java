//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.inventory;


import com.bazaarbot.ICommodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory {
    private double maxSize;

    private HashMap<ICommodity, InventoryEntry> expecting;
    private HashMap<ICommodity, InventoryEntry> stuff;
    private HashMap<ICommodity, Double> ideal;

    public Inventory() {
        this.stuff = new HashMap<>();
        this.ideal = new HashMap<>();
        this.expecting = new HashMap<>();
        this.maxSize = 0;
    }

    public Inventory(Inventory src) {
        this.stuff = new HashMap<>(src.stuff);
        this.ideal = new HashMap<>(src.ideal);
        this.expecting = new HashMap<>(src.expecting);
        this.maxSize = src.maxSize;
    }

    public void fromData(InventoryData data) {
        List<ICommodity> sizes = new ArrayList<>();
        List<InventoryEntry> amountsp = new ArrayList<>();
        for (ICommodity key : data.getStart().keySet()) {
            sizes.add(key);
            amountsp.add(new InventoryEntry(data.getStart().get(key), 0));
        }

        for (int i = 0; i < sizes.size(); i++) {
            stuff.put(sizes.get(i), amountsp.get(i));
        }


        sizes = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        for (ICommodity key : data.getIdeal().keySet()) {
            sizes.add(key);
            amounts.add(data.getIdeal().get(key));

            for (int i = 0; i < sizes.size(); i++) {
                ideal.put(sizes.get(i), amounts.get(i));
            }
        }

        maxSize = data.getMaxSize();
    }

    /**
     * Returns how much of this
     *
     * @param good string id of commodity
     * @return
     */
    public double query(ICommodity good) {
        if (stuff.containsKey(good)) {
            return stuff.get(good).getAmount();
        }

        return 0;
    }

    /**
     * Returns how much of this expected
     *
     * @param good string id of commodity
     * @return
     */
    public double queryExpecting(ICommodity good) {
        if (expecting.containsKey(good)) {
            return expecting.get(good).getAmount();
        }

        return 0;
    }

    public double query_cost(ICommodity good) {
        if (stuff.containsKey(good)) {
            return stuff.get(good).getOriginalPrice();
        }

        return 0;
    }

    public double getEmptySpace() {
        return maxSize - getUsedSpace();
    }

    public double getUsedSpace() {
        double spaceUsed = 0;
        for (ICommodity key : stuff.keySet()) {
            spaceUsed += stuff.get(key).getAmount() * key.getSpace();
        }
        return spaceUsed;
    }

    /**
     * Change the amount of the given commodity by delta
     *
     * @param good  string id of commodity
     * @param delta amount added
     */
    public double change(ICommodity good, double delta, double unitCost) {
        double resultAmount;
        double resultPrice;

        if (stuff.containsKey(good)) {
            InventoryEntry current = stuff.get(good);
            if (unitCost > 0) {
                //If we did not have any previous inventory for this item
                if (current.getAmount() <= 0) {
                    resultAmount = delta;
                    resultPrice = unitCost;
                } else {
                    //original_price = Average the two costs
                    resultPrice = (current.getAmount() * current.getOriginalPrice() + delta * unitCost) / (current.getAmount() + delta);
                    resultAmount = current.getAmount() + delta;
                }
            } else {
                resultAmount = current.getAmount() + delta;
                resultPrice = current.getOriginalPrice();
            }
        } else {
            //just copy from old value?
            resultAmount = delta;
            resultPrice = unitCost;
        }
        if (resultAmount < 0) {
            resultAmount = 0;
            resultPrice = 0;
        }

        stuff.put(good, new InventoryEntry(resultAmount, resultPrice));
        return resultPrice;
    }

    /**
     * Change the amount of the expected commodity by delta
     *
     * @param good  string id of commodity
     * @param delta amount added
     */
    public double changeExpecting(ICommodity good, double delta, double unit_cost) {
        double resultAmount = 0;
        double resultPrice = 0;

        if (expecting.containsKey(good)) {
            InventoryEntry current = expecting.get(good);
            if (unit_cost > 0) {
                //If we did not have any previous inventory for this item
                if (current.getAmount() <= 0) {
                    resultAmount = delta;
                    resultPrice = unit_cost;
                } else {
                    //original_price = Average the two costs
                    resultPrice = (current.getAmount() * current.getOriginalPrice() + delta * unit_cost) / (current.getAmount() + delta);
                    resultAmount = current.getAmount() + delta;
                }
            } else {
                resultAmount = current.getAmount() + delta;
                resultPrice = current.getOriginalPrice();
            }
        } else {
            //just copy from old value?
            resultAmount = delta;
            resultPrice = unit_cost;
        }
        if (resultAmount < 0) {
            resultAmount = 0;
            resultPrice = 0;
        }

        expecting.put(good, new InventoryEntry(resultAmount, resultPrice));
        return resultPrice;
    }

    //return current unit cost

    /**
     * Returns # of units above the desired inventory level, or 0 if @ or below
     *
     * @param good string id of commodity
     * @return
     */
    public double surplus(ICommodity good) {
        double amount = query(good);
        double ideal = 0;

        if (this.ideal.containsKey(good))
            ideal = this.ideal.get(good);

        if (amount > ideal) {
            return (amount - ideal);
        }

        return 0;
    }

    /**
     * Returns # of units below the desired inventory level, or 0 if @ or above
     *
     * @param good
     * @return
     */
    public double shortage(ICommodity good) {
        if (!stuff.containsKey(good)) {
            return 0;
        }

        double amount = query(good) + queryExpecting(good);

        double ideal = 0;
        if (this.ideal.containsKey(good))
            ideal = this.ideal.get(good);

        if (amount < ideal) {
            return (ideal - amount);
        }

        return 0;
    }

    private static final class InventoryEntry {
        private final double amount;
        private final double originalPrice;

        InventoryEntry(double amount, double origialPrice) {
            this.amount = amount;
            this.originalPrice = origialPrice;
        }

        double getAmount() {
            return amount;
        }

        double getOriginalPrice() {
            return originalPrice;
        }
    }
}


