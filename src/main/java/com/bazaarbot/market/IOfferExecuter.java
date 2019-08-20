package com.bazaarbot.market;

public interface IOfferExecuter {
    OfferExecutionStatistics execute(Offer bid, Offer ask);

    void rejectBid(Offer offer, double unitPrice);

    void rejectAsk(Offer offer, double unitPrice);
}
