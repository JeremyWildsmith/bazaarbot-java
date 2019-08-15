package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;

final class CommodityPricingRange {
    private final double min;
    private final double max;
    private final ICommodity commodity;

    CommodityPricingRange(ICommodity commodity, double min, double max) {
        this.commodity = commodity;
        this.min = min;
        this.max = max;
    }

    double getMin() {
        return min;
    }

    double getMax() {
        return max;
    }


    double positionInRange(double value) {
        return positionInRange(value, true);
    }

    double positionInRange(double value, boolean clamp) {
        value -= min;

        double workingMax = max - min;
        double workingMin = 0;

        value = (value / (workingMax - workingMin));

        if (clamp) {
            if (value < 0) {
                value = 0;
            }

            if (value > 1) {
                value = 1;
            }

        }

        return value;
    }

    ICommodity getCommodity() {
        return commodity;
    }
}
