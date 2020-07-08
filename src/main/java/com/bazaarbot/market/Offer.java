//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.ImmutableAgent;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.statistics.ICloneable;

import java.math.BigDecimal;

public class Offer implements ICloneable<Offer> {
    private ICommodity commodity;
    //the thing offered
    private double units;
    //how many units
    private BigDecimal unitPrice;
    //price per unit
    private IAgent agent;

    private final long createdTimeOffer = System.nanoTime();

    //who offered this
    public Offer(IAgent agent, ICommodity commodity, double units, BigDecimal unitPrice) {
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
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

    @Override
    public Offer clone() {
        Offer offer;
        try {
            offer = (Offer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cannot clone Offer!", e);
        }
        offer.setAgent(new ImmutableAgent(agent));
        return offer;
    }
}


