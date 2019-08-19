package com.bazaarbot.contract;

import com.bazaarbot.agent.IAgent;

import java.util.Map;

public class ProvideContract implements IContract {
    private final IAgent reciever;
    private final Map<String, Double> goods;

    public ProvideContract(IAgent reciever, IContract sellContract, Map<String, Double> goods) {
        this.reciever = reciever;
        this.goods = goods;
    }

    @Override
    public void complete() {

    }

    @Override
    public void abandon() {

    }
}
