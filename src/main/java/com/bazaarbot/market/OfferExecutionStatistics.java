package com.bazaarbot.market;

public class OfferExecutionStatistics {
    public final double unitsTraded;
    public final double moneyTraded;

    public OfferExecutionStatistics(double unitsTraded, double moneyTraded) {
        this.unitsTraded = unitsTraded;
        this.moneyTraded = moneyTraded;
    }
}
