package com.bazaarbot.contract;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.IAgent;

public interface IContract {
    void complete();

    void abandon();

    IAgent getSeller();
    IAgent getBuyer();

    double getContractPrice();

    long getContractTimeResolution();

    boolean isCommodityPresent(ICommodity commodity);

    double getQuantityTraded();

}
