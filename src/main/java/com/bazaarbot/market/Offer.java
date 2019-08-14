//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.ICommodity;

public class Offer
{
    public ICommodity good;
    //the thing offered
    public double units;
    //how many units
    public double unit_price;
    //price per unit
    public int agent_id;
    //who offered this
    public Offer(int agent_id_, ICommodity commodity_, double units_, double unit_price_) {
        agent_id = agent_id_;
        good = commodity_;
        units = units_;
        unit_price = unit_price_;
    }

    public String toString() {
        return "(" + agent_id + "): " + good + "x " + units + " @ " + unit_price;
    }

}


