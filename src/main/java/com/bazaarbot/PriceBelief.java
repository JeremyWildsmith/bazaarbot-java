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


    public double min(int window) {
        double min = 99999999;
        //Math.POSITIVE_INFINITY;
        if (window > observed.size())
            window = observed.size();

        for (int i = 0;i < window - 1;i++)
        {
            Double f = observed.get(observed.size() - 1 - i);
            if (f < min)
            {
                min = f;
            }

        }
        return min;
    }

    public double max(int window) {
        double max = -9999999;
        /**
         * Math.NEGATIVE_INFINITY;
         */
        if (window > observed.size())
            window = observed.size();

        for (int i = 0;i < window - 1;i++)
        {
            Double f = observed.get(observed.size() - 1 - i);
            if (f > max)
            {
                max = f;
            }

        }
        return max;
    }

    public PriceRange observe(int window) {
        return new PriceRange(this.min(window), this.max(window));
    }

    public void addTransaction(double unitPrice, boolean success) {
        if(success)
            observed.add(unitPrice);
    }
}
