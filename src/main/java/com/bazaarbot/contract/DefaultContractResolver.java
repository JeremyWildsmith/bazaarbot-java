package com.bazaarbot.contract;

import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.Offer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DefaultContractResolver implements IContractResolver {

    @Override
    public IContract signContract(IAgent seller, IAgent buyer, ICommodity good, double units, BigDecimal clearingPrice) {
        seller.removeCommodity(good, units);
        buyer.addCommodity(good, units);
        Map<ICommodity, Double> contractGoods = new HashMap<>();
        contractGoods.put(good, units);
        return new DefaultContract(buyer, seller, contractGoods, clearingPrice);
    }


    @Override
    public IContractNegotiator getContractNegotiator(DefaultMarket market, Offer bidOffer, Offer askOffer) {
        return new DefaultContractNegotiator(market, bidOffer, askOffer);
    }

}
