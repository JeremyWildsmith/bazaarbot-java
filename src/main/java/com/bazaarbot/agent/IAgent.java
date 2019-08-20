//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

public interface IAgent {

    void simulate(Market market);

    Offer createBid(Market market, ICommodity commodity, double limit);

    Offer createAsk(Market market, ICommodity commodity, double limit);

    void generateOffers(Market market, ICommodity commodity);

    void updatePriceModel(String act, ICommodity commodity, boolean success, double unitPrice);

    void addInventoryItem(ICommodity good, double amount);

    double queryInventory(ICommodity commodity);

    void produceInventory(ICommodity good, double delta);

    void consumeInventoryItem(ICommodity commodity, double amount);

    void changeInventory(ICommodity commodity, double amount, double unitCost);

    AgentSnapshot getSnapshot();

    boolean isInventoryFull();

    double getLastSimulateProfit();

    String getAgentName();

    double getMoneyAvailable();

    void setMoneyAvailable(double value);
}


