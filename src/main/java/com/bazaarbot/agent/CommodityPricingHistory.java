package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

final class CommodityPricingHistory {
    private final List<Double> observed = new ArrayList<>();
    private final ICommodity commodity;

    CommodityPricingHistory(ICommodity commodity) {
        this(commodity, 2.0, 6.0);
    }

    CommodityPricingHistory(ICommodity commodity, double... prices) {
        this.commodity = commodity;
        for (double price : prices) {
            this.observed.add(price);
        }
    }

    private double min(int window) {
        if (window > observed.size()) {
            window = observed.size();
        }
        OptionalDouble optionalDouble = IntStream.range(0, window).mapToDouble(d -> d).min();
        return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 0;
    }

    private double max(int window) {
        if (window > observed.size()) {
            window = observed.size();
        }
        OptionalDouble optionalDouble = IntStream.range(0, window).mapToDouble(d -> d).max();
        return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 0;
    }

    CommodityPricingRange observe(int window) {
        return new CommodityPricingRange(commodity, min(window), max(window));
    }

    void addTransaction(double unitPrice) {
        observed.add(unitPrice);
    }

    ICommodity getCommodity() {
        return commodity;
    }
}