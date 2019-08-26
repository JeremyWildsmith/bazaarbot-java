package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultSimulationStrategy;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.Offer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DefaultContractResolver implements IContractResolver {
    private Statistics statistics;

    @Override
    public IContract signContract(IAgent seller, IAgent buyer, ICommodity good, double units, BigDecimal clearingPrice) {
        seller.addCommodity(good, -units);
        buyer.addCommodity(good, units);
        Map<ICommodity, Double> contractGoods = new HashMap<>();
        contractGoods.put(good, units);
        return new DefaultContract(buyer, seller, contractGoods, clearingPrice);
    }


    @Override
    public IContractNegotiator getContractNegotiator(DefaultMarket market, Offer bidOffer, Offer askOffer) {
        return new DefaultContractNegotiator(market, bidOffer, askOffer, statistics);
    }

    @Override
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
