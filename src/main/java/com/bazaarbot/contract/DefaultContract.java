package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

import java.util.Map;

public class DefaultContract implements IContract {
    private final IAgent reciever;
    private final Map<ICommodity, Double> goods;

    public DefaultContract(IAgent reciever, IAgent seller, Map<ICommodity, Double> goods) {
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
