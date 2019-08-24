package com.bazaarbot;

import com.bazaarbot.agent.AgentSimulation;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;

/**
 * @author Nick Gritsenko
 */
public interface IEconomy {
    void startSimulation();

    void simulateAgentActivity();

    void addAgent(IAgent agent, AgentSimulation agentSimulation);

    void addAgent(IMarket market, IAgent agent, AgentSimulation agentSimulation);

    void addMarket(IMarket market);

    Statistics getStatistics();
}
