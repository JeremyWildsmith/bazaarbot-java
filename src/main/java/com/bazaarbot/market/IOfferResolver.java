package com.bazaarbot.market;

import com.bazaarbot.ICommodity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IOfferResolver {
    Map<ICommodity, OfferResolutionStatistics> resolve(IOfferExecuter executor, Map<ICommodity, List<Offer>> bids, Map<ICommodity, List<Offer>> asks);
}
