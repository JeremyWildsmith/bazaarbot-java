package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public interface IContractResolver {
    IContract newContract(IAgent provider, IAgent receiver, ICommodity good, double units, double clearingPrice);

    ContractQuote getQuote(IAgent buyer, IAgent seller, double space);
}
