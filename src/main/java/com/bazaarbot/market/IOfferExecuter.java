package com.bazaarbot.market;

import java.util.List;

public interface IOfferExecuter {
    OfferExecutionStatistics execute(Offer bid, Offer ask);
    void rejectBid(Offer offer);
    void rejectAsk(Offer offer);
}
