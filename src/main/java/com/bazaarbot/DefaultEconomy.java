package com.bazaarbot;

import com.bazaarbot.agent.AgentSimulation;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.DefaultContractResolver;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.HistoryRegistry;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Gritsenko
 */
public class DefaultEconomy implements IEconomy {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEconomy.class);

    private Map<IMarket, HistoryRegistry> marketsMap = new HashMap<>();
    private Map<IAgent, AgentSimulation> agents = new HashMap<>();
    private Map<IMarket, List<IAgent>> agentsToMarket = new HashMap<>();

    private final Statistics statistics = new Statistics();
    private final String name;

    public DefaultEconomy() {
        this("DefaultEconomy");
    }

    public DefaultEconomy(String name) {
        this.name = name;
    }

    @Override
    public void startSimulation() {
        IContractResolver contractResolver = new DefaultContractResolver(statistics);
        for (IMarket market : marketsMap.keySet()) {
            simulateAgentActivity();
            market.step(contractResolver, marketsMap.get(market));
            // outputs what is left from session
            // e.g. unmet offers
            // next round they will be served in priority, because they were created earlier
            //
            // Put statistics, which affects market prices depending the left offers, quantities from the round and prices
        }
        for (IMarket market : marketsMap.keySet()) {
            LOG.info("Market {} statistics. Bid offers left {}, ask offers left {}",
                    market, market.getBidOffersSize(), market.getAskOffersSize());
        }
    }

    @Override
    public void simulateAgentActivity() {
        for (IMarket market : marketsMap.keySet()) {
            List<IAgent> agentsToMarketList = agentsToMarket.get(market);
            for (IAgent agent : (agentsToMarketList == null ? agents.keySet() : agentsToMarketList)) {
                agents.get(agent).simulateActivity(agent, market, statistics);
            }
        }
    }
    @Override
    public void addAgent(IAgent agent, AgentSimulation agentSimulation) {
        agents.put(agent, agentSimulation);
    }

    @Override
    public void addAgent(IMarket market, IAgent agent, AgentSimulation agentSimulation) {
        addAgent(agent,agentSimulation);
        List<IAgent> agents = agentsToMarket.get(market);
        if (agents == null) {
            agents = new ArrayList<>();
        }
        agents.add(agent);
        agentsToMarket.put(market, agents);
    }

    @Override
    public void addMarket(IMarket market) {
        HistoryRegistry registry = new HistoryRegistry();
        statistics.addHistoryRegistry(market, registry);
        marketsMap.put(market, registry);
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return name;
    }
}
