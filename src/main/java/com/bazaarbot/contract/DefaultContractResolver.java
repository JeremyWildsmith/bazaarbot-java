package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public class DefaultContractResolver implements IContractResolver {

    @Override
    public void newContract(IAgent provider, IAgent receiver, ICommodity good, Double units, Double clearing_price) {
        IAgent seller = provider;
        IAgent buyer = receiver;
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearing_price);
    }

    @Override
    public ContractQuote getQuote(IAgent source, IAgent dest, double space) {
        return new ContractQuote(0,0,0);
    }
}
