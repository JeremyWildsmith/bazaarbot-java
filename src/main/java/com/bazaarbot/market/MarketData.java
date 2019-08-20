//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

import java.util.List;

public class MarketData   
{
    public List<ICommodity> goods;
    public List<IAgent> agents;

    public MarketData(List<ICommodity> goods, List<IAgent> agents) {
        this.goods = goods;
        this.agents = agents;
    }
}
