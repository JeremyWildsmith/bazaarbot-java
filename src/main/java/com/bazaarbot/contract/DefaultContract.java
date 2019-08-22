package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

import java.util.Map;
import java.util.stream.Collectors;

public class DefaultContract implements IContract {
    private final IAgent buyer;
    private final IAgent seller;
    private final Map<ICommodity, Double> goods;
    private final double clearingPrice;
    private final long contractTimeResolution = System.nanoTime();

    public DefaultContract(IAgent buyer, IAgent seller, Map<ICommodity, Double> goods, double clearingPrice) {
        this.buyer = buyer;
        this.seller = seller;
        this.goods = goods;
        this.clearingPrice = clearingPrice;
    }

    @Override
    public void complete() {

    }

    @Override
    public void abandon() {

    }

    @Override
    public IAgent getBuyer() {
        return buyer;
    }

    @Override
    public double getContractPrice() {
        return clearingPrice;
    }

    @Override
    public long getContractTimeResolution() {
        return contractTimeResolution;
    }

    @Override
    public boolean isCommodityPresent(ICommodity commodity) {
        return goods.containsKey(commodity);
    }

    @Override
    public double getQuantityTraded() {
        return goods.values().stream().mapToDouble(d -> d).sum();
    }

    @Override
    public IAgent getSeller() {
        return seller;
    }


}
