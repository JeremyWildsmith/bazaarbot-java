package com.bazaarbot.economy;

import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;
import org.greenrobot.eventbus.EventBus;

/**
 * @author Nick Gritsenko
 */
public interface IEconomy {
    void startSimulation();

    void addAgent(IAgent agent);

    void addMarket(IMarket market);

    void setContractResolver(IContractResolver resolver);

    Statistics getStatistics();

    void setUserEventBus(EventBus eventBus);

}