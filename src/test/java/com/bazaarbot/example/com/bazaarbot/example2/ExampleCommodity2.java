package com.bazaarbot.example.com.bazaarbot.example2;

import com.bazaarbot.ICommodity;

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
