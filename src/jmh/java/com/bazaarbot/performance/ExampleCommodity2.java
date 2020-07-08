package com.bazaarbot.performance;

import com.bazaarbot.commodity.ICommodity;

/**
 * @author Nick Gritsenko
 */
public class ExampleCommodity2 implements ICommodity {
    @Override
    public String getName() {
        return "ExampleCommodity2";
    }

    @Override
    public double getSpace() {
        return 2;
    }

    @Override
    public String toString() {
        return getName();
    }
}
