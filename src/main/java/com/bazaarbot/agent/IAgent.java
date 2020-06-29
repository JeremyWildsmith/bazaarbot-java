//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.market.IMarket;

import java.math.BigDecimal;
import java.util.List;

public interface IAgent {

    void addCommodity(ICommodity good, double amount);
    void removeCommodity(ICommodity good, double amount);

    double getCommodityAmount(ICommodity commodity);

    List<ICommodity> getAvailableCommodities();

    boolean isInventoryFull();

    boolean checkInventorySpace(double amount);

    String getAgentName();

    BigDecimal getMoneyAvailable();

    void setMoneyAvailable(BigDecimal moneyAvailable);

    void simulateActivity(IMarket market);
}


