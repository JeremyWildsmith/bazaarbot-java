package com.bazaarbot.example.com.bazaarbot.example2;

import com.bazaarbot.ICommodity;

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
