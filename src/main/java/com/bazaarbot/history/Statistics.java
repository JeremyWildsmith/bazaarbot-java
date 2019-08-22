package com.bazaarbot.history;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.Market2;
import com.bazaarbot.market.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class Statistics {
    private static final Logger LOG = LoggerFactory.getLogger(Statistics.class);
    private final Map<Market2, IHistoryRegistryRead> statisticsRegistry = new HashMap<>();

    private final ForkJoinPool threadPool = new ForkJoinPool(10);

    private class PricingTuple {
        private final ICommodity commodity;
        private final double price;


        private PricingTuple(ICommodity commodity, double price) {
            this.commodity = commodity;
            this.price = price;
        }

        public ICommodity getCommodity() {
            return commodity;
        }

        public double getPrice() {
            return price;
        }
    }

    private class AgentTuple {
        private final IAgent agent;
        private final double profit;

        public AgentTuple(IAgent agent, double profit) {
            this.agent = agent;
            this.profit = profit;
        }

        public IAgent getAgent() {
            return agent;
        }

        public double getProfit() {
            return profit;
        }
    }

    public void addHistoryRegistry(Market2 market, IHistoryRegistryRead registry) {
        statisticsRegistry.put(market, registry);
    }

    private IHistoryRegistryRead getHistoryRegistryByMarket(Market2 market) {
        return Optional.of(statisticsRegistry.get(market)).orElseThrow();
    }

    public double getCommodityCount(Market2 market, ICommodity commodity) {
        return getCommodityCount(market, commodity, 0, Long.MAX_VALUE);
    }

    public double getCommodityCount(Market2 market, ICommodity commodity, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getBidOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .filter(offer -> offer.getCommodity().equals(commodity))
                .mapToDouble(Offer::getUnits)
                .sum()
        ).join();
    }

    public double getAverageHistoricalPrice(Market2 market, ICommodity commodity) {
        return getAverageHistoricalPrice(market, commodity, 0, Long.MAX_VALUE);
    }

    private Predicate<IContract> contractTimeResolutionPredicate(long start, long end) {
        return (contract) -> {
            long contractTimeResolution = contract.getContractTimeResolution();
            return contractTimeResolution >= start && contractTimeResolution <= end;
        };
    }

    private Predicate<Offer> offerTimeCreatedPredicate(long start, long end) {
        return (offer) -> {
            long offerTimeCreated = offer.getCreatedTimeOffer();
            return offerTimeCreated >= start && offerTimeCreated <= end;
        };
    }

    public double getAverageHistoricalPrice(Market2 market, ICommodity commodity, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getContractAgreements().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(contract -> contract.isCommodityPresent(commodity))
                .filter(contractTimeResolutionPredicate(start, end))
                .mapToDouble(IContract::getContractPrice)
                .average()
                .orElse(0);
    }

    public ICommodity getHottestCommodity(Market2 market) {
        return getHottestCommodity(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getHottestCommodity(Market2 market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getAskOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .collect(Collectors.groupingBy(PricingTuple::getCommodity,
                        Collectors.summingDouble(PricingTuple::getPrice)))
                .entrySet().parallelStream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null)
        ).join();
    }

    public ICommodity getCheapestCommodity(Market2 market) {
        return getCheapestCommodity(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getCheapestCommodity(Market2 market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getBidOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .min(Comparator.comparingDouble(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null)
        ).join();
    }

    public ICommodity getDearestGood(Market2 market) {
        return getDearestGood(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getDearestGood(Market2 market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getBidOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .max(Comparator.comparingDouble(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null)
        ).join();
    }

    public IAgent getMostProfitableAgent(Market2 market) {
        return getMostProfitableAgent(market, 0, Long.MAX_VALUE);
    }

    public IAgent getMostProfitableAgent(Market2 market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getContractAgreements().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(contractTimeResolutionPredicate(start, end))
                .map(contract -> new AgentTuple(contract.getSeller(), contract.getContractPrice()))
                .collect(Collectors.groupingBy(AgentTuple::getAgent,
                        Collectors.summingDouble(AgentTuple::getProfit)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
