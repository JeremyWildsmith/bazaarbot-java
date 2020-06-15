package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.Offer;

import java.math.BigDecimal;

public interface IContractResolver {
    IContract signContract(IAgent provider, IAgent receiver, ICommodity good, double units, BigDecimal clearingPrice);

    IContractNegotiator getContractNegotiator(DefaultMarket market, Offer bidOffer, Offer askOffer);

    void setStatistics(Statistics statistics);
}
