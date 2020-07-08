package com.bazaarbot.contract;

import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.statistics.ICloneable;

import java.math.BigDecimal;

public interface IContract extends ICloneable<IContract> {

    IAgent getSeller();
    IAgent getBuyer();

    BigDecimal getContractPrice();

    long getContractTimeResolution();

    boolean isCommodityPresent(ICommodity commodity);

    double getQuantityTraded();

}
