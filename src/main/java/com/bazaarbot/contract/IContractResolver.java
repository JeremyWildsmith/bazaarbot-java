package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

import java.math.BigDecimal;

public interface IContractResolver {
    IContract signContract(IAgent provider, IAgent receiver, ICommodity good, double units, BigDecimal clearingPrice);

    IContractNegotiator getContractNegotiator(Market market, Offer bidOffer, Offer askOffer);
}
