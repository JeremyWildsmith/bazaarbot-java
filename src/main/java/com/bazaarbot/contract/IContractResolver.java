package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.BasicAgent;

public interface IContractResolver {
    void newContract(BasicAgent provider, BasicAgent receiver, ICommodity good, Double units, Double clearing_price);
}
