package com.bazaarbot.history;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class Statistics {
    private static final Logger LOG = LoggerFactory.getLogger(Statistics.class);
    private final Map<Market, IHistoryRegistryRead> statisticsRegistry = new HashMap<>();

    private final ForkJoinPool threadPool = new ForkJoinPool(10);

    private class PricingTuple {
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

    private class AgentTuple {
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

    public void addHistoryRegistry(Market market, IHistoryRegistryRead registry) {
        statisticsRegistry.put(market, registry);
    }

    public IHistoryRegistryRead getHistoryRegistryByMarket(Market market) {
        IHistoryRegistryRead registry = statisticsRegistry.get(market);
        if (registry == null) {
            throw new RuntimeException("Registry is null");
        }
        LOG.debug("Sizes - bids: {} asks: {} contracts: {}", registry.getBidOffers().size(),
                registry.getAskOffers().size(), registry.getContractAgreements().size());
        return registry;
    }

    public double getCommodityCount(Market market, ICommodity commodity) {
        return getCommodityCount(market, commodity, 0, Long.MAX_VALUE);
    }

    public double getCommodityCount(Market market, ICommodity commodity, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getBidOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .filter(offer -> offer.getCommodity().equals(commodity))
                .mapToDouble(Offer::getUnits)
                .sum()
        ).join();
    }

    public BigDecimal getAverageHistoricalPrice(Market market, ICommodity commodity) {
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

    public BigDecimal getAverageHistoricalPrice(Market market, ICommodity commodity, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getContractAgreements().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(contract -> contract.isCommodityPresent(commodity))
                .filter(contractTimeResolutionPredicate(start, end))
                .map(IContract::getContractPrice)
                .collect(new BigDecimalAverageCollector());
    }

    public ICommodity getHottestCommodity(Market market) {
        return getHottestCommodity(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getHottestCommodity(Market market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getAskOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .collect(Collectors.groupingBy(PricingTuple::getCommodity,
                        Collectors.reducing(BigDecimal.ZERO, PricingTuple::getPrice, BigDecimal::add)))
                .entrySet().parallelStream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null)
        ).join();
    }

    public ICommodity getCheapestCommodity(Market market) {
        return getCheapestCommodity(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getCheapestCommodity(Market market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getBidOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .min(Comparator.comparing(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null)
        ).join();
    }

    public ICommodity getDearestGood(Market market) {
        return getDearestGood(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getDearestGood(Market market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return threadPool.submit(() -> registry.getBidOffers().parallelStream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .max(Comparator.comparing(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null)
        ).join();
    }

    public IAgent getMostProfitableAgent(Market market) {
        return getMostProfitableAgent(market, 0, Long.MAX_VALUE);
    }

    public IAgent getMostProfitableAgent(Market market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getContractAgreements().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(contractTimeResolutionPredicate(start, end))
                .map(contract -> new AgentTuple(contract.getSeller(), contract.getContractPrice()))
                .collect(Collectors.groupingBy(AgentTuple::getAgent,
                        Collectors.reducing(BigDecimal.ZERO, AgentTuple::getProfit, BigDecimal::add)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public List<Offer> getBidOffers(Market market) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getBidOffers().stream()
                .map(HistoryRecord::getHistoryObject)
                .collect(Collectors.toList());
    }
}
