package com.bazaarbot.market;

public class OfferResolutionStatistics {
    public final int offersResolved;
    public final double moneyTraded;

    public OfferResolutionStatistics(int offersResolved, double moneyTraded) {
        this.offersResolved = offersResolved;
        this.moneyTraded = moneyTraded;
    }
}
