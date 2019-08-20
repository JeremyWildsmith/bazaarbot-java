package com.bazaarbot.example;

import com.bazaarbot.ICommodity;

public enum ExampleCommodity implements ICommodity {
    Food("food", 1.0),
    Tools("tools", 1.0),
    Wood("wood", 0.0),
    Work("work", 0.0),
    Metal("metal", 0.0),
    Ore("ore", 0.0);

    private String name;
    private double space;

    ExampleCommodity(String name, double space) {
        this.name = name;
        this.space = space;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getSpace() {
        return space;
    }
}
