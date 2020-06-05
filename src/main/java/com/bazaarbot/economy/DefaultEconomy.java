package com.bazaarbot.economy;

import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.HistoryRegistry;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Gritsenko
 */
public class DefaultEconomy implements IEconomy {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEconomy.class);

    private IContractResolver contractResolver;
    private EventBus userEventBus;
    private final Statistics statistics = new Statistics();
    private final HistoryRegistry registry = new HistoryRegistry();

    private List<IMarket> markets = new ArrayList<>();
    private List<IAgent> agents = new ArrayList<>();

    private final String name;

    public DefaultEconomy() {
        this("DefaultEconomy");
    }

    public DefaultEconomy(String name) {
        this.name = name;
    }

    @Override
    public void startSimulation() {
        for (IMarket market : markets) {
            statistics.addHistoryRegistry(market, registry);
            for (IAgent agent : agents) {
                agent.simulateActivity(market, statistics);
            }
            market.step(contractResolver, registry, userEventBus);
            LOG.debug("Market {} statistics. Bid offers left {}, ask offers left {}, contracts signed {}",
                    market, market.getBidOffersSize(), market.getAskOffersSize(), statistics.getSignedContracts(market));
            // outputs what is left from session
            // e.g. unmet offers
            // next round they will be served in priority, because they were created earlier
            //
            // Put statistics, which affects market prices depending the left offers, quantities from the round and prices
        }
    }

    @Override
    public void addAgent(IAgent agent) {
        agents.add(agent);
    }

    @Override
    public void addMarket(IMarket market) {
        markets.add(market);
    }

    @Override
    public void setContractResolver(IContractResolver resolver) {
        this.contractResolver = resolver;
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public void setUserEventBus(EventBus eventBus) {
        this.userEventBus = eventBus;
    }

    @Override
    public String toString() {
        return name;
    }
}
