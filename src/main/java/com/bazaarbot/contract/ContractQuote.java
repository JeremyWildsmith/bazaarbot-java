package com.bazaarbot.contract;

public class ContractQuote {
    private final double cost;
    private final double risk;
    private final double deliveryTime;

    public ContractQuote(double cost, double risk, double deliveryTime) {
        this.cost = cost;
        this.risk = risk;
        this.deliveryTime = deliveryTime;
    }

    public double getCost() {
        return cost;
    }

    public double getRisk() {
        return risk;
    }

    public double getDeliveryTime() {
        return deliveryTime;
    }
}
