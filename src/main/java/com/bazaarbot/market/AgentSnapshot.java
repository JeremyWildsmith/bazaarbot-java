package com.bazaarbot.market;

public class AgentSnapshot {
    private final String name;
    private final double money;

    public AgentSnapshot(String name, double money) {
        this.name = name;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }
}
