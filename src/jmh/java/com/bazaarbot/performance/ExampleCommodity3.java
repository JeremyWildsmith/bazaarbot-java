package com.bazaarbot.performance;

import com.bazaarbot.commodity.ICommodity;

/**
 * @author Nick Gritsenko
 */
public class ExampleCommodity3 implements ICommodity {
    @Override
    public String getName() {
        return "ExampleCommodity3";
    }

    @Override
    public double getSpace() {
        return 3;
    }

    @Override
    public String toString() {
        return getName();
    }
}
