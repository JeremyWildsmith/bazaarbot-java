package com.bazaarbot.history;

import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.Offer;

import java.util.List;

/**
 * @author Nick Gritsenko
 */
public interface IHistoryRegistryRead {
    List<HistoryRecord<Offer>> getBidOffers();
    List<HistoryRecord<Offer>> getAskOffers();
    List<HistoryRecord<IContract>> getContractAgreements();
}
