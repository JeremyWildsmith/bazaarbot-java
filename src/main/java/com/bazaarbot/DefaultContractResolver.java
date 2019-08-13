package com.bazaarbot;

public class DefaultContractResolver implements IContractResolver {
    @Override
    public void newContract(BasicAgent provider, BasicAgent receiver, String good, Double units, Double clearing_price) {
        BasicAgent seller = provider;
        BasicAgent buyer = receiver;
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearing_price);
    }
}
