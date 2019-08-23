package com.bazaarbot;

import com.bazaarbot.agent.AgentSimulation2;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.DefaultContractResolver;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.HistoryRegistry;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.Market2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Gritsenko
 */
public class Economy2 {
    private static final Logger LOG = LoggerFactory.getLogger(Economy2.class);

    private Map<Market2, HistoryRegistry> marketsMap = new HashMap<>();
    private List<IAgent> agents = new ArrayList<>();
    private Map<IAgent, AgentSimulation2> agentSimulations = new HashMap<>();

    private final Statistics statistics = new Statistics();
    private final String name;

    public Economy2() {
        this("DefaultEconomy");
    }

    public Economy2(String name) {
        this.name = name;
//        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
//        root.setLevel(Level.OFF);
    }

    public void startSimulation() {
        IContractResolver contractResolver = new DefaultContractResolver(statistics);
        for (Market2 market : marketsMap.keySet()) {
            simulateAgentActivity();
            market.step(contractResolver, marketsMap.get(market));
            // outputs what is left from session
            // e.g. unmet offers
            // next round they will be served in priority, because they were created earlier
            //
            // Put statistics, which affects market prices depending the left offers, quantities from the round and prices
        }
        for (Market2 market : marketsMap.keySet()) {
            LOG.info("Market {} statistics. Bid offers left {}, ask offers left {}",
                    market, market.getBidOffersSize(), market.getAskOffersSize());
        }
    }

    public void simulateAgentActivity() {
        for (Market2 market : marketsMap.keySet()) {
            for (IAgent agent : agents) {
                agent.simulate(market);
                agentSimulations.get(agent).simulateActivity(agent, market, statistics);
            }
        }
    }

    public void addAgent(IAgent agent) {
        agents.add(agent);
    }

    public void addMarket(Market2 market) {
        HistoryRegistry registry = new HistoryRegistry();
        statistics.addHistoryRegistry(market, registry);
        marketsMap.put(market, registry);
    }

    public void addAgentSimulation(IAgent agent, AgentSimulation2 agentSimulation) {
        agentSimulations.put(agent, agentSimulation);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return name;
    }
}
