package com.bazaarbot.example.com.bazaarbot.example2;

import com.bazaarbot.ICommodity;

/**
 * @author Nick Gritsenko
 */
public class ExampleCommodity1 implements ICommodity {
    @Override
    public String getName() {
        return "ExampleCommodity1";
    }

    @Override
    public double getSpace() {
        return 1;
    }

    @Override
    public String toString() {
        return getName();
    }
}
