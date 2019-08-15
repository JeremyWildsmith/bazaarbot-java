//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.inventory;

import com.bazaarbot.ICommodity;

import java.util.HashMap;

/**
 *
 */
public class InventoryData {
    private double maxSize;
    private HashMap<ICommodity, Double> ideal;
    private HashMap<ICommodity, Double> start;

    public InventoryData(double maxSize, HashMap<ICommodity, Double> ideal, HashMap<ICommodity, Double> start) {
        this.maxSize = maxSize;
        this.ideal = ideal;
        this.start = start;
    }

    public double getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(double maxSize) {
        this.maxSize = maxSize;
    }

    public HashMap<ICommodity, Double> getIdeal() {
        return ideal;
    }

    public void setIdeal(HashMap<ICommodity, Double> ideal) {
        this.ideal = ideal;
    }

    public HashMap<ICommodity, Double> getStart() {
        return start;
    }

    public void setStart(HashMap<ICommodity, Double> start) {
        this.start = start;
    }
}