//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot.agent;

import com.bazaarbot.inventory.InventoryData;
import com.bazaarbot.Logic;

/**
 * The most fundamental agent class, and has as little implementation as possible.
 * In most cases you should start by extending DefaultAgent instead of this.
 * @author larsiusprime
 */
public class AgentData   
{
    private String className;
    private double money;
    private InventoryData inventory;
    private Logic logic;
    private Integer lookBack;

    public AgentData(String className, double money, Logic logic) {
        this.className = className;
        this.money = money;
        this.logic = logic;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public InventoryData getInventory() {
        return inventory;
    }

    public void setInventory(InventoryData inventory) {
        this.inventory = inventory;
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public Integer getLookBack() {
        return lookBack;
    }

    public void setLookBack(Integer lookBack) {
        this.lookBack = lookBack;
    }
}


