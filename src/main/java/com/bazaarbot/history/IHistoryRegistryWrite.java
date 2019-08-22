package com.bazaarbot.history;

import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.Offer;

import java.util.List;

/**
 * @author Nick Gritsenko
 */
public interface IHistoryRegistryWrite {
    void addBidOffer(Offer bidOffer);
    void addAskOffer(Offer askOffer);
    void addContract(IContract contract);


    void addAskOffers(List<Offer> askOffers);

    void addBidOffers(List<Offer> bidOffers);
}
