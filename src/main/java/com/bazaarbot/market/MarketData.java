//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.AgentData;
import com.bazaarbot.agent.BasicAgent;

import java.util.List;

public class MarketData {
    private List<ICommodity> goods;
    private List<AgentData> agentTypes;
    private List<BasicAgent> agents;

    public MarketData(List<ICommodity> goods, List<AgentData> agentTypes, List<BasicAgent> agents) {
        this.goods = goods;
        this.agentTypes = agentTypes;
        this.agents = agents;
    }

    public List<ICommodity> getGoods() {
        return goods;
    }

    public void setGoods(List<ICommodity> goods) {
        this.goods = goods;
    }

    public List<AgentData> getAgentTypes() {
        return agentTypes;
    }

    public void setAgentTypes(List<AgentData> agentTypes) {
        this.agentTypes = agentTypes;
    }

    public List<BasicAgent> getAgents() {
        return agents;
    }

    public void setAgents(List<BasicAgent> agents) {
        this.agents = agents;
    }
}
