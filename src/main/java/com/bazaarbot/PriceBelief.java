package com.bazaarbot;

import java.util.ArrayList;
import java.util.List;

public final class PriceBelief {
    private final List<Double> observed;

    public PriceBelief() {
        observed = new ArrayList<>();

        int price = 2;
        observed.add(price * 1.0);
        observed.add(price * 3.0);
    }

    public PriceBelief(PriceBelief source) {
        observed = new ArrayList<>(source.observed);
    }

    public Point observe(int window) {
        return new Point(Quick.minArr(observed,window),Quick.maxArr(observed,window));
    }

    public void addTransaction(double unitPrice, boolean success) {
        if(success)
            observed.add(unitPrice);
    }
}
