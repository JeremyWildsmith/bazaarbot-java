package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public interface IContractResolver {
    void newContract(IAgent provider, IAgent receiver, ICommodity good, Double units, Double clearing_price);

    ContractQuote getQuote(IAgent source, IAgent dest, double space);
}
