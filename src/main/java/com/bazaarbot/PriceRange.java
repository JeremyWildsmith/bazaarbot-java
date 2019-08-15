package com.bazaarbot;

public class PriceRange {
    private final double min;
    private final double max;

    public PriceRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }


    public double positionInRange(double value) {
        return positionInRange(value, true);
    }

    public double positionInRange(double value, boolean clamp) {
        value -= min;

        double workingMax = max - min;
        double workingMin = 0;

        value = (value / (workingMax - workingMin));

        if (clamp)
        {
            if (value < 0)
            {
                value = 0;
            }

            if (value > 1)
            {
                value = 1;
            }

        }

        return value;
    }
}
