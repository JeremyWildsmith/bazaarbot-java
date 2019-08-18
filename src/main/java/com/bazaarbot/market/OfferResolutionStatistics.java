package com.bazaarbot.market;

import com.bazaarbot.ICommodity;

import java.util.List;
import java.util.Map;

public class OfferResolutionStatistics {
    public final int offersResolved;
    public final double moneyTraded;
    public final List<OfferExecutionStatistics> resolvedOffers;
    public final double unitsTraded;

    public OfferResolutionStatistics(List<OfferExecutionStatistics> resolvedOffers) {
        this.offersResolved = resolvedOffers.size();
        this.resolvedOffers = resolvedOffers;

        double unitsTraded = 0;
        double moneyTraded = 0;

        for(OfferExecutionStatistics e : resolvedOffers) {
            unitsTraded += e.unitsTraded;
            moneyTraded += e.moneyTraded;
        }

        this.unitsTraded = unitsTraded;
        this.moneyTraded = moneyTraded;
    }
}
