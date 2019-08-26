package com.bazaarbot.agent;

import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.IMarket;

/**
 * @author Nick Gritsenko
 */
public interface ISimulationStrategy {
    void simulateActivity(IAgent agent, IMarket market, Statistics statistics);
}
