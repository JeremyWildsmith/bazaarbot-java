//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public class Offer {
    private ICommodity good;
    //the thing offered
    private double units;
    //how many units
    private double unitPrice;
    //price per unit
    private IAgent agent;

    private final long timePut = System.nanoTime();

    //who offered this
    public Offer(IAgent agent, ICommodity commodity, double units, double unitPrice) {
        this.agent = agent;
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

    public IAgent getAgent() {
        return agent;
    }

    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    public long getTimePut() {
        return timePut;
    }

    public String toString() {
        return "(" + agent + "): " + good + " x " + units + " @ " + unitPrice;
    }

}


