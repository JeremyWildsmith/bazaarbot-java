//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;


import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.BasicAgent;

public class Offer
{
    public final ICommodity good;
    //the thing offered
    public double units;
    //how many units
    public final double unit_price;
    //price per unit
    public final BasicAgent owner;
    //who offered this
    public Offer(BasicAgent owner_, ICommodity commodity_, double units_, double unit_price_) {
        owner = owner_;
        good = commodity_;
        units = units_;
        unit_price = unit_price_;
    }

    public String toString() {
        return "(" + owner.toString() + "): " + good + "x " + units + " @ " + unit_price;
    }

}


