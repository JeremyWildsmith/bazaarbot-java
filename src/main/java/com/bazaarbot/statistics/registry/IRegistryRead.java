package com.bazaarbot.statistics.registry;

import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.Offer;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Nick Gritsenko
 */
public interface IRegistryRead {
    Stream<Offer> getBidOffersStream();
    Stream<Offer> getAskOffersStream();
    Stream<IContract> getContractAgreementsStream();
}
