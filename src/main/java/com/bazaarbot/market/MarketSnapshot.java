//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.history.History;

import java.util.List;

public class MarketSnapshot
{
    private final History history;
    private final List<AgentSnapshot> agents;

    public MarketSnapshot(History history, List<AgentSnapshot> agents) {
        this.history = history;
        this.agents = agents;
    }

    public History getHistory() {
        return history;
    }

    public List<AgentSnapshot> getAgents() {
        return agents;
    }
}


