package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public class DefaultOfferExecutor implements IOfferExecuter {
    @Override
    public OfferExecutionStatistics execute(Offer buyerOffer, Offer sellerOffer) {
        Double quantityTraded = Math.min(buyerOffer.getUnits(), sellerOffer.getUnits());
        Double clearingPrice = sellerOffer.getUnitPrice();
        ICommodity good = buyerOffer.getGood();

        transferGood(good, quantityTraded, sellerOffer.getAgent(), buyerOffer.getAgent(), clearingPrice);
        transferMoney(quantityTraded * clearingPrice, sellerOffer.getAgent(), buyerOffer.getAgent());
        //update agent price beliefs based on successful transaction
        IAgent buyer = buyerOffer.getAgent();
        IAgent seller = sellerOffer.getAgent();
        buyer.updatePriceModel("buy", good, true, clearingPrice);
        seller.updatePriceModel("sell", good, true, clearingPrice);
        //log the stats
        double moneyTraded = (quantityTraded * clearingPrice);

        return new OfferExecutionStatistics(quantityTraded, moneyTraded);
    }

    @Override
    public void rejectBid(Offer buyer, double unitPrice) {
        buyer.getAgent().updatePriceModel("buy", buyer.getGood(), false, unitPrice);
    }

    @Override
    public void rejectAsk(Offer seller, double unitPrice) {
        seller.getAgent().updatePriceModel("sell", seller.getGood(), false, unitPrice);
    }

    //sort by id so everything works again
    //_agents.Sort(Utils.sortAgentId);
    private void transferGood(ICommodity good, double units, IAgent seller, IAgent buyer, double clearingPrice) {
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearingPrice);
    }

    private void transferMoney(double amount, IAgent seller, IAgent buyer) {
        seller.setMoneyAvailable(seller.getMoneyAvailable() + amount);
        buyer.setMoneyAvailable(buyer.getMoneyAvailable() - amount);
    }
}
