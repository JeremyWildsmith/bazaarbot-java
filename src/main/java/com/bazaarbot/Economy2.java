package com.bazaarbot;

import com.bazaarbot.agent.AgentSimulation2;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.market.Market2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Gritsenko
 */
public class Economy2 {
    private List<Market2> markets = new ArrayList<>();
    private List<IAgent> agents = new ArrayList<>();
    private Map<IAgent, AgentSimulation2> agentsSimulation = new HashMap<>();

    public Economy2() {

    }

    public void startSimulation(int rounds) {
        for (Market2 market : markets) {
            market.startSession(rounds, this::simulateAgentActivity);
        }
    }

    public void simulateAgentActivity() {
        for (Market2 market : markets) {
            for (IAgent agent : agents) {
                agent.simulate(market);
                agentsSimulation.get(agent).simulateActivity(agent, market);
            }
        }
    }

    public void addAgent(IAgent agent) {
        agents.add(agent);
    }

    public void addMarket(Market2 market) {
        markets.add(market);
    }

    public void addAgentSimulation(IAgent agent, AgentSimulation2 agentSimulation) {
        agentsSimulation.put(agent, agentSimulation);
    }


}
