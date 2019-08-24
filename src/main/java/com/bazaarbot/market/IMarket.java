package com.bazaarbot.market;

import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.IHistoryRegistryWrite;

/**
 * @author Nick Gritsenko
 */
public interface IMarket {
    void putBid(Offer offer);

    void putAsk(Offer offer);

    int getBidOffersSize();

    int getAskOffersSize();

    void step(IContractResolver contractResolver, IHistoryRegistryWrite registry);
}
