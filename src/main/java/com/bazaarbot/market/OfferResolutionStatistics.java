package com.bazaarbot.market;

import java.util.List;

public class OfferResolutionStatistics {
    private final int offersResolved;
    private final double moneyTraded;
    private final List<OfferExecutionStatistics> resolvedOffers;
    private final double unitsTraded;

    public OfferResolutionStatistics(List<OfferExecutionStatistics> resolvedOffers) {
        this.offersResolved = resolvedOffers.size();
        this.resolvedOffers = resolvedOffers;

        double unitsTraded = 0;
        double moneyTraded = 0;

        for (OfferExecutionStatistics e : resolvedOffers) {
            unitsTraded += e.getUnitsTraded();
            moneyTraded += e.getMoneyTraded();
        }

        this.unitsTraded = unitsTraded;
        this.moneyTraded = moneyTraded;
    }

    public int getOffersResolved() {
        return offersResolved;
    }

    public double getMoneyTraded() {
        return moneyTraded;
    }

    public List<OfferExecutionStatistics> getResolvedOffers() {
        return resolvedOffers;
    }

    public double getUnitsTraded() {
        return unitsTraded;
    }
}
