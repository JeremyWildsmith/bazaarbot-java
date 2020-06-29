package com.bazaarbot.economy;

import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.market.IMarket;

import java.util.List;

/**
 * @author Nick Gritsenko
 */
public interface IEconomy {
    void startSimulation();

    void addAgent(IAgent agent);

    void addMarket(IMarket market);

    void setContractResolver(IContractResolver resolver);

    List<IMarket> getMarketList();

}
