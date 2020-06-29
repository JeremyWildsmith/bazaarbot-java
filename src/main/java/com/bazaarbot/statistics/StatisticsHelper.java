package com.bazaarbot.statistics;

import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.agent.ImmutableAgent;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.market.Offer;
import com.bazaarbot.statistics.registry.IRegistryRead;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class StatisticsHelper {

    private StatisticsHelper(){}

    public static long getSignedContractsCount(IMarket market) {
        IRegistryRead registry = market.getRegistry();
        return registry.getContractAgreementsStream().count();
    }

    public static long getAskOffersCount(IMarket market) {
        IRegistryRead registry = market.getRegistry();
        return registry.getAskOffersStream().count();
    }

    public static long getBidOffersCount(IMarket market) {
        IRegistryRead registry = market.getRegistry();
        return registry.getBidOffersStream().count();
    }

    public static double getCommodityCount(IMarket market, ICommodity commodity) {
        return getCommodityCount(market, commodity, 0, Long.MAX_VALUE);
    }

    public static double getCommodityCount(IMarket market, ICommodity commodity, long start, long end) {
        IRegistryRead registry = market.getRegistry();
        return registry.getBidOffersStream()
                .filter(offerTimeCreatedPredicate(start, end))
                .filter(offer -> offer.getCommodity().equals(commodity))
                .mapToDouble(Offer::getUnits)
                .sum();
    }

    public static List<Offer> getBidOffers(IMarket market) {
        IRegistryRead registry = market.getRegistry();
        return registry.getBidOffersStream()
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<Offer> getAskOffers(IMarket market) {
        IRegistryRead registry = market.getRegistry();
        return registry.getAskOffersStream()
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<IContract> getSignedContracts(IMarket market) {
        IRegistryRead registry = market.getRegistry();
        return registry.getContractAgreementsStream()
                .collect(Collectors.toUnmodifiableList());
    }

    public static BigDecimal getAverageHistoricalPrice(IMarket market, ICommodity commodity) {
        return getAverageHistoricalPrice(market, commodity, 0, Long.MAX_VALUE);
    }

    private static Predicate<IContract> contractTimeResolutionPredicate(long start, long end) {
        return (contract) -> {
            long contractTimeResolution = contract.getContractTimeResolution();
            return contractTimeResolution >= start && contractTimeResolution <= end;
        };
    }

    private static Predicate<Offer> offerTimeCreatedPredicate(long start, long end) {
        return (offer) -> {
            long offerTimeCreated = offer.getCreatedTimeOffer();
            return offerTimeCreated >= start && offerTimeCreated <= end;
        };
    }

    public static BigDecimal getAverageHistoricalPrice(IMarket market, ICommodity commodity, long start, long end) {
        IRegistryRead registry = market.getRegistry();
        return registry.getContractAgreementsStream()
                .filter(contract -> contract.isCommodityPresent(commodity))
                .filter(contractTimeResolutionPredicate(start, end))
                .map(IContract::getContractPrice)
                .collect(new BigDecimalAverageCollector());
    }

    public static ICommodity getHottestCommodity(IMarket market) {
        return getHottestCommodity(market, 0, Long.MAX_VALUE);
    }

    public static ICommodity getHottestCommodity(IMarket market, long start, long end) {
        IRegistryRead registry = market.getRegistry();
        return registry.getAskOffersStream()
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .collect(Collectors.groupingBy(PricingTuple::getCommodity,
                        Collectors.reducing(BigDecimal.ZERO, PricingTuple::getPrice, BigDecimal::add)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static ICommodity getCheapestCommodity(IMarket market) {
        return getCheapestCommodity(market, 0, Long.MAX_VALUE);
    }

    public static ICommodity getCheapestCommodity(IMarket market, long start, long end) {
        IRegistryRead registry = market.getRegistry();
        return registry.getBidOffersStream()
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .min(Comparator.comparing(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null);
    }

    public static ICommodity getDearestGood(IMarket market) {
        return getDearestGood(market, 0, Long.MAX_VALUE);
    }

    public static ICommodity getDearestGood(IMarket market, long start, long end) {
        IRegistryRead registry = market.getRegistry();
        return registry.getBidOffersStream()
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .max(Comparator.comparing(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null);
    }

    public static ImmutableAgent getMostProfitableAgent(IMarket market) {
        return getMostProfitableAgent(market, 0, Long.MAX_VALUE);
    }

    public static ImmutableAgent getMostProfitableAgent(IMarket market, long start, long end) {
        IRegistryRead registry = market.getRegistry();
        return (ImmutableAgent) registry.getContractAgreementsStream()
                .filter(contractTimeResolutionPredicate(start, end))
                .map(contract -> new AgentTuple(contract.getSeller(), contract.getContractPrice()))
                .collect(Collectors.groupingBy(AgentTuple::getAgent,
                        Collectors.reducing(BigDecimal.ZERO, AgentTuple::getProfit, BigDecimal::add)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static class PricingTuple {
        private final ICommodity commodity;
        private final BigDecimal price;


        private PricingTuple(ICommodity commodity, BigDecimal price) {
            this.commodity = commodity;
            this.price = price;
        }

        ICommodity getCommodity() {
            return commodity;
        }

        BigDecimal getPrice() {
            return price;
        }
    }

    private static class AgentTuple {
        private final IAgent agent;
        private final BigDecimal profit;

        private AgentTuple(IAgent agent, BigDecimal profit) {
            this.agent = agent;
            this.profit = profit;
        }

        IAgent getAgent() {
            return agent;
        }

        BigDecimal getProfit() {
            return profit;
        }
    }

}
