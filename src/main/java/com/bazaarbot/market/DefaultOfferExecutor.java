package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.BasicAgent;

public class DefaultOfferExecutor implements IOfferExecuter {
    @Override
    public OfferExecutionStatistics execute(Offer buyer, Offer seller) {
        Double quantityTraded = Math.min(buyer.units, seller.units);
        Double clearingPrice = seller.unit_price;
        ICommodity good = buyer.good;

        transferGood(good, quantityTraded,seller.agent,buyer.agent, clearingPrice);
        transferMoney(quantityTraded * clearingPrice, seller.agent, buyer.agent);
        //update agent price beliefs based on successful transaction
        BasicAgent buyer_a = buyer.agent;
        BasicAgent seller_a = seller.agent;
        buyer_a.updatePriceModel("buy", good, true, clearingPrice);
        seller_a.updatePriceModel("sell", good, true, clearingPrice);
        //log the stats
        double moneyTraded = (quantityTraded * clearingPrice);

        return new OfferExecutionStatistics(quantityTraded, moneyTraded);
    }

    @Override
    public void rejectBid(Offer buyer) {
        buyer.agent.updatePriceModel("buy", buyer.good, false);
    }

    @Override
    public void rejectAsk(Offer seller) {
        seller.agent.updatePriceModel("sell", seller.good, false);
    }

    //sort by id so everything works again
    //_agents.Sort(Utils.sortAgentId);
    private void transferGood(ICommodity good, double units, BasicAgent seller, BasicAgent buyer, double clearing_price) {
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearing_price);
    }

    private void transferMoney(double amount, BasicAgent seller, BasicAgent buyer) {
        seller.setMoney(seller.getMoney() + amount);
        buyer.setMoney(buyer.getMoney() - amount);
    }
}
