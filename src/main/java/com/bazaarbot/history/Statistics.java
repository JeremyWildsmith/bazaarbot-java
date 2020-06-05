package com.bazaarbot.history;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.market.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Nick Gritsenko
 */
public class Statistics {
    private static final Logger LOG = LoggerFactory.getLogger(Statistics.class);
    private final Map<IMarket, IHistoryRegistryRead> statisticsRegistry = new ConcurrentHashMap<>();

    public int getSignedContracts(IMarket market) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getContractAgreements().size();
    }

    public int getAskOffers(IMarket market) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getAskOffers().size();
    }

    public Set<IMarket> getAvailableMarkets() {
        return statisticsRegistry.keySet();
    }

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

    public void addHistoryRegistry(IMarket market, IHistoryRegistryRead registry) {
        statisticsRegistry.put(market, registry);
    }

    public IHistoryRegistryRead getHistoryRegistryByMarket(IMarket market) {
        IHistoryRegistryRead registry = statisticsRegistry.get(market);
        if (registry == null) {
            throw new RuntimeException("Registry is null");
        }
        LOG.trace("Sizes - bids: {} asks: {} contracts: {}", registry.getBidOffers().size(),
                registry.getAskOffers().size(), registry.getContractAgreements().size());
        return registry;
    }

    public double getCommodityCount(IMarket market, ICommodity commodity) {
        return getCommodityCount(market, commodity, 0, Long.MAX_VALUE);
    }

    public double getCommodityCount(IMarket market, ICommodity commodity, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getBidOffers().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .filter(offer -> offer.getCommodity().equals(commodity))
                .mapToDouble(Offer::getUnits)
                .sum();
    }

    public BigDecimal getAverageHistoricalPrice(IMarket market, ICommodity commodity) {
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

    public BigDecimal getAverageHistoricalPrice(IMarket market, ICommodity commodity, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getContractAgreements().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(contract -> contract.isCommodityPresent(commodity))
                .filter(contractTimeResolutionPredicate(start, end))
                .map(IContract::getContractPrice)
                .collect(new BigDecimalAverageCollector());
    }

    public ICommodity getHottestCommodity(IMarket market) {
        return getHottestCommodity(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getHottestCommodity(IMarket market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getAskOffers().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .collect(Collectors.groupingBy(PricingTuple::getCommodity,
                        Collectors.reducing(BigDecimal.ZERO, PricingTuple::getPrice, BigDecimal::add)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public ICommodity getCheapestCommodity(IMarket market) {
        return getCheapestCommodity(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getCheapestCommodity(IMarket market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getBidOffers().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .min(Comparator.comparing(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null);
    }

    public ICommodity getDearestGood(IMarket market) {
        return getDearestGood(market, 0, Long.MAX_VALUE);
    }

    public ICommodity getDearestGood(IMarket market, long start, long end) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getBidOffers().stream()
                .map(HistoryRecord::getHistoryObject)
                .filter(offerTimeCreatedPredicate(start, end))
                .map(offer -> new PricingTuple(offer.getCommodity(), offer.getUnitPrice()))
                .max(Comparator.comparing(PricingTuple::getPrice))
                .map(PricingTuple::getCommodity)
                .orElse(null);
    }

    public IAgent getMostProfitableAgent(IMarket market) {
        return getMostProfitableAgent(market, 0, Long.MAX_VALUE);
    }

    public IAgent getMostProfitableAgent(IMarket market, long start, long end) {
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

    public List<Offer> getBidOffers(IMarket market) {
        IHistoryRegistryRead registry = getHistoryRegistryByMarket(market);
        return registry.getBidOffers().stream()
                .map(HistoryRecord::getHistoryObject)
                .collect(Collectors.toList());
    }
}
