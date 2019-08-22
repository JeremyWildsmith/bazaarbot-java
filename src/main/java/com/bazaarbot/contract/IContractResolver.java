package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.market.Market2;
import com.bazaarbot.market.Offer;

public interface IContractResolver {
    IContract newContract(IAgent provider, IAgent receiver, ICommodity good, double units, double clearingPrice);

    ContractQuote getQuote(Market2 market, Offer bidOffer, Offer askOffer);
}
