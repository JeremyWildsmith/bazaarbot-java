package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public class DefaultContractResolver implements IContractResolver {

    @Override
    public void newContract(IAgent seller, IAgent buyer, ICommodity good, Double units, Double clearing_price) {
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearing_price);
    }

    @Override
    public ContractQuote getQuote(IAgent source, IAgent dest, double space) {
        return new ContractQuote(0, 0, 0);
    }
}
