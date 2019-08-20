//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;
import com.bazaarbot.*;
import com.bazaarbot.inventory.Inventory;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

import java.util.HashMap;

public interface IAgent
{

    void simulate(Market market);

    void generateOffers(Market bazaar, ICommodity good);
    void updatePriceModel(String act, ICommodity good, boolean success, double unitPrice);
    Offer createBid(Market bazaar, ICommodity good, double limit);
    Offer createAsk(Market bazaar, ICommodity commodity_, double limit_);

    double queryInventory(ICommodity good);

    void produceInventory(ICommodity good, double delta);;

    void consumeInventory(ICommodity good, double delta);

    void changeInventory(ICommodity good, double delta, double unit_cost);

    default void updatePriceModel(String buy, ICommodity good, boolean b) {
        updatePriceModel(buy, good, b, 0);
    }

    AgentSnapshot getSnapshot();

    boolean isInventoryFull();

    double getLastSimulateProfit();

    String getClassName();

    double getMoney();

    void setMoney(double value);
}


