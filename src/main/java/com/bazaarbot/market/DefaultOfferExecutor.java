package com.bazaarbot.market;

import com.bazaarbot.agent.BasicAgent;

public class DefaultOfferExecutor implements IOfferExecuter {
    @Override
    public OfferExecutionStatistics execute(Offer bid, Offer ask) {
        Double quantity_traded = Math.min(seller.units,buyer.units);
        return null;
    }

    @Override
    public void rejectBid(Offer buyer) {
        buyer.agent.updatePriceModel("buy", buyer.good, false);
    }

    @Override
    public void rejectAsk(Offer seller) {
        seller.agent.updatePriceModel("sell", seller.good, false);
    }
}
