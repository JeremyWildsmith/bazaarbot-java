package com.bazaarbot.market;

public class OfferExecutionStatistics {
    private final double unitsTraded;
    private final double moneyTraded;

    public OfferExecutionStatistics(double unitsTraded, double moneyTraded) {
        this.unitsTraded = unitsTraded;
        this.moneyTraded = moneyTraded;
    }

    public double getUnitsTraded() {
        return unitsTraded;
    }

    public double getMoneyTraded() {
        return moneyTraded;
    }
}
