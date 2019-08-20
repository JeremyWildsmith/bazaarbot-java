package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

import java.util.HashMap;
import java.util.Map;

public class DefaultContractResolver implements IContractResolver {

    @Override
    public IContract newContract(IAgent seller, IAgent buyer, ICommodity good, double units, double clearingPrice) {
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearingPrice);
        Map<ICommodity, Double> contractGoods = new HashMap<>();
        contractGoods.put(good, units);
        return new DefaultContract(buyer, seller, contractGoods);
    }

    @Override
    public ContractQuote getQuote(IAgent buyer, IAgent seller, double space) {
        return new ContractQuote(0, 0, 0);
    }
}
