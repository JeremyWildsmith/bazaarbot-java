//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public class Offer {
    private ICommodity commodity;
    //the thing offered
    private double units;
    //how many units
    private double unitPrice;
    //price per unit
    private IAgent agent;

    private final long createdTimeOffer = System.nanoTime();

    //who offered this
    public Offer(IAgent agent, ICommodity commodity, double units, double unitPrice) {
        this.agent = agent;
        this.commodity = commodity;
        this.units = units;
        this.unitPrice = unitPrice;
    }

    public ICommodity getCommodity() {
        return commodity;
    }

    public void setCommodity(ICommodity commodity) {
        this.commodity = commodity;
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

    public long getCreatedTimeOffer() {
        return createdTimeOffer;
    }

    public String toString() {
        return "(" + agent + "): " + commodity + " x " + units + " @ " + unitPrice;
    }

}


