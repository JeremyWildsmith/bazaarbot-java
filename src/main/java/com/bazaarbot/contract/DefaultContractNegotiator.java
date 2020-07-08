package com.bazaarbot.contract;

import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.statistics.StatisticsHelper;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class DefaultContractNegotiator implements IContractNegotiator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContractNegotiator.class);

    private final Offer bidOffer;
    private final Offer askOffer;
    private final DefaultMarket market;

    DefaultContractNegotiator(DefaultMarket market, Offer bidOffer, Offer askOffer) {
        this.market = market;
        this.bidOffer = bidOffer;
        this.askOffer = askOffer;
    }

    private boolean hasMoney(double quantity, BigDecimal unitPrice, IAgent agent) {
        boolean hasMoney = unitPrice.multiply(new BigDecimal(quantity)).compareTo(agent.getMoneyAvailable()) < 0;
        if (!hasMoney) {
            LOG.debug("Buyer {} does not have enough money for purchase ({} * {} == {} vs {}).",
                    agent, quantity, unitPrice, unitPrice.multiply(new BigDecimal(quantity)), agent.getMoneyAvailable());
        }
        return hasMoney;
    }

    @Override
    public BigDecimal negotiateContractPrice() {
        BigDecimal sellerPrice = bidOffer.getUnitPrice();
        BigDecimal buyerPrice = askOffer.getUnitPrice();
        double desiredQuantity = askOffer.getUnits();
        ICommodity commodity = askOffer.getCommodity();
        // 1. Check if prices are good enough
        LOG.debug("Trying to make a deal. SellerPrice: {} BuyerPrice: {}", sellerPrice, buyerPrice);
        if (sellerPrice.compareTo(buyerPrice) > 0) {
            // 1.1 Get total commodity count and see if desired quantity is more or equal then half of market size
            double totalCountOfCommodity = StatisticsHelper.getCommodityCount(market, commodity);
            LOG.debug("May be buyer is a bulk trader? Total market count for commodity {}: {}. Desired amount: {}",
                    askOffer.getCommodity(), totalCountOfCommodity, desiredQuantity);
            if (desiredQuantity >= totalCountOfCommodity / 2 && hasMoney(desiredQuantity, sellerPrice, askOffer.getAgent())) {
                LOG.debug("DEAL. Trading in bulk.");
                // 1.1.1 It's a bulk trade, we can try to agree on prices
                bidOffer.setUnitPrice(sellerPrice);
                return sellerPrice;
            } else {
                BigDecimal averageCommodityPrice = StatisticsHelper.getAverageHistoricalPrice(market, commodity);
                if (averageCommodityPrice.compareTo(BigDecimal.ZERO) == 0) {
                    LOG.debug("There is no history for the commodity {}. Try to see if the commodity is hot on the market.", commodity);
                    ICommodity hotCommodity = StatisticsHelper.getHottestCommodity(market);
                    LOG.debug("Hottest commodity is {}", hotCommodity);
                    if (hotCommodity != null && hotCommodity.equals(commodity)
                            && hasMoney(desiredQuantity, sellerPrice, askOffer.getAgent())) {
                        LOG.debug("Commodity {} is hot, we will by at any price", commodity);
                        return sellerPrice;
                    }
                }
                LOG.debug("Trying to negotiate with average market price.");
                // 1.1.2 It's not a bulk trade, prices are really high for the trade.
                // May be we can have an agreement based on average price?
                // Which is closer to average - seller or buyer?

                LOG.debug("Average historical price for commodity {}: {}", commodity, averageCommodityPrice);
                BigDecimal closeToA = averageCommodityPrice.subtract(sellerPrice).abs();
                BigDecimal closeToB = averageCommodityPrice.subtract(buyerPrice).abs();
                boolean closingDeal = closeToA.compareTo(closeToB) >= 0;
                LOG.debug("Buyer is closer to average (Seller >= Buyer)? {} >= {} == {}", closeToA, closeToB, closingDeal);
                if (closingDeal && hasMoney(desiredQuantity, buyerPrice, askOffer.getAgent())) {
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
        else if (hasMoney(desiredQuantity, buyerPrice, askOffer.getAgent())) {
            LOG.debug("DEAL. Pricing is good");
            // 2.1 Just let's trade!
            return buyerPrice;
        } else {
            return BigDecimal.ZERO;
        }
    }
}
