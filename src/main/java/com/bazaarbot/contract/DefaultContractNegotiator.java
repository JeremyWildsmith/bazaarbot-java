package com.bazaarbot.contract;

import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class DefaultContractNegotiator implements IContractNegotiator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContractNegotiator.class);
    private final Statistics statistics;

    private final Offer bidOffer;
    private final Offer askOffer;
    private final Market market;

    DefaultContractNegotiator(Market market, Statistics statistics, Offer bidOffer, Offer askOffer) {
        this.market = market;
        this.statistics = statistics;
        this.bidOffer = bidOffer;
        this.askOffer = askOffer;
    }

    @Override
    public BigDecimal negotiateContractPrice() {
        BigDecimal sellerPrice = bidOffer.getUnitPrice();
        BigDecimal buyerPrice = askOffer.getUnitPrice();
        double desiredQuantity = askOffer.getUnits();
        // 1. Check if prices are good enough
        LOG.debug("Trying to make a deal. SellerPrice: {} BuyerPrice: {}", sellerPrice, buyerPrice);
        if (sellerPrice.compareTo(buyerPrice) > 0) {
            // 1.1 Get total commodity count and see if desired quantity is more or equal then half of market size
            double totalCountOfCommodity = statistics.getCommodityCount(market, askOffer.getCommodity());
            LOG.debug("May be buyer is a bulk trader? Total market count for commodity {}: {}. Desired amount: {}",
                    askOffer.getCommodity(), totalCountOfCommodity, desiredQuantity);
            if (desiredQuantity >= totalCountOfCommodity / 2) {
                LOG.debug("DEAL. Trading in bulk.");
                // 1.1.1 It's a bulk trade, we can try to agree on prices
                bidOffer.setUnitPrice(sellerPrice);
                return sellerPrice;
            } else {
                LOG.debug("Not a bulk trader. Trying to negotiate with average market price.");
                // 1.1.2 It's not a bulk trade, prices are really high for the trade.
                // May be we can have an agreement based on average price?
                // Which is closer to average - seller or buyer?
                BigDecimal averageCommodityPrice = statistics.getAverageHistoricalPrice(market, askOffer.getCommodity());
                LOG.debug("Average historical price for commodity {}: {}", askOffer.getCommodity(), averageCommodityPrice);
                BigDecimal closeToA = averageCommodityPrice.subtract(sellerPrice).abs();
                BigDecimal closeToB = averageCommodityPrice.subtract(buyerPrice).abs();
                boolean closingDeal = closeToA.compareTo(closeToB) >= 0;
                LOG.debug("Buyer is closer to average (Seller >= Buyer)? {} >= {} == {}", closeToA, closeToB, closingDeal);
                if (closingDeal) {
                    LOG.debug("DEAL. Buyer has a good price.");
                    // 1.1.2.1 We are good to go! Buyer price is more stick to average.
                    bidOffer.setUnitPrice(buyerPrice);
                    return buyerPrice;
                } else {
                    LOG.debug("No deal. Leaving.");
                    // 1.1.2.2 No way to this, buyer price is just too high
                    return BigDecimal.ZERO;
                }
            }
        }
        // 2. Prices are good
        else {
            LOG.debug("DEAL. Pricing is good");
            // 2.1 Just let's trade!
            return buyerPrice;
        }
    }
}
