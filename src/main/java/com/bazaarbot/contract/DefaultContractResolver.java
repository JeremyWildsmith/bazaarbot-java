package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DefaultContractResolver implements IContractResolver {
    private final Statistics statistics;

    public DefaultContractResolver(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public IContract signContract(IAgent seller, IAgent buyer, ICommodity good, double units, BigDecimal clearingPrice) {
        seller.addCommodity(good, -units);
        buyer.addCommodity(good, units);
        Map<ICommodity, Double> contractGoods = new HashMap<>();
        contractGoods.put(good, units);
        return new DefaultContract(buyer, seller, contractGoods, clearingPrice);
    }


    @Override
    public IContractNegotiator getContractNegotiator(Market market, Offer bidOffer, Offer askOffer) {
        return new DefaultContractNegotiator(market, statistics, bidOffer, askOffer);
    }
}
