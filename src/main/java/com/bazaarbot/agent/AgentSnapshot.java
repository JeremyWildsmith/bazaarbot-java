package com.bazaarbot.agent;

import com.bazaarbot.inventory.Inventory;

public class AgentSnapshot {
    private final String className;
    private final double money;
    private final Inventory inventory;

    public AgentSnapshot(String className, double money, Inventory inventory) {
        this.money = money;
        this.inventory = inventory;
        this.className = className;
    }

    public double getMoney() {
        return money;
    }

    public String getClassName() {
        return className;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
