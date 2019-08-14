//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import java.util.List;

public class MarketData   
{
    public List<Good> goods;
    public List<AgentData> agentTypes;
    public List<BasicAgent> agents;

    public MarketData(List<Good> goods, List<AgentData> agentTypes, List<BasicAgent> agents) {
        this.goods = goods;
        this.agentTypes = agentTypes;
        this.agents = agents;
    }
}
