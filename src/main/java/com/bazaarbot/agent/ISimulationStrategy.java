package com.bazaarbot.agent;

import com.bazaarbot.market.IMarket;

/**
 * @author Nick Gritsenko
 */
public interface ISimulationStrategy {
    void simulateActivity(IAgent agent, IMarket market);
}
