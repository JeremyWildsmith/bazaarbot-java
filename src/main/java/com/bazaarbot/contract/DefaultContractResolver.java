package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.Market2;
import com.bazaarbot.market.Offer;

import java.util.HashMap;
import java.util.Map;

public class DefaultContractResolver implements IContractResolver {
    private final Statistics statistics;

    public DefaultContractResolver(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public IContract newContract(IAgent seller, IAgent buyer, ICommodity good, double units, double clearingPrice) {
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearingPrice);
        Map<ICommodity, Double> contractGoods = new HashMap<>();
        contractGoods.put(good, units);
        return new DefaultContract(buyer, seller, contractGoods, clearingPrice);
    }


    @Override
    public ContractQuote getQuote(Market2 market, Offer bidOffer, Offer askOffer) {
        return new ContractQuote(market, statistics, bidOffer, askOffer);
    }
}
