//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public class Offer
{
    public ICommodity good;
    //the thing offered
    public double units;
    //how many units
    public double unit_price;
    //price per unit
    public IAgent agent;
    //who offered this

    public Offer(IAgent agent, ICommodity commodity_, double units_, double unit_price_) {
        this.agent = agent;
        good = commodity_;
        units = units_;
        unit_price = unit_price_;
    }

    public String toString() {
        return "(" + agent.toString() + "): " + good + "x " + units + " @ " + unit_price;
    }

}


