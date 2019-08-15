//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.ICommodity;

public class Offer {
    private ICommodity good;
    //the thing offered
    private double units;
    //how many units
    private double unitPrice;
    //price per unit
    private int agentId;

    //who offered this
    public Offer(int agentId, ICommodity commodity, double units, double unitPrice) {
        this.agentId = agentId;
        this.good = commodity;
        this.units = units;
        this.unitPrice = unitPrice;
    }

    public ICommodity getGood() {
        return good;
    }

    public void setGood(ICommodity good) {
        this.good = good;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String toString() {
        return "(" + agentId + "): " + good + "x " + units + " @ " + unitPrice;
    }

}


