package com.bazaarbot.contract;

import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.agent.ImmutableAgent;

import java.math.BigDecimal;
import java.util.Map;

public class DefaultContract implements IContract {
    private final IAgent buyer;
    private final IAgent seller;
    private final Map<ICommodity, Double> goods;
    private final BigDecimal clearingPrice;
    private final long contractTimeResolution = System.nanoTime();

    DefaultContract(IAgent buyer, IAgent seller, Map<ICommodity, Double> goods, BigDecimal clearingPrice) {
        this.buyer = new ImmutableAgent(buyer);
        this.seller = new ImmutableAgent(seller);
        this.goods = goods;
        this.clearingPrice = clearingPrice;
    }

    @Override
    public IAgent getBuyer() {
        return buyer;
    }

    @Override
    public BigDecimal getContractPrice() {
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

    @Override
    public IContract clone() {
        //contract is immutable
        return this;
    }
}
