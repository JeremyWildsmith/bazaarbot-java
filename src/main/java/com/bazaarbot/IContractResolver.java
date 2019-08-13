package com.bazaarbot;

import java.util.HashMap;

public interface IContractResolver {
    void newContract(BasicAgent provider, BasicAgent receiver, String good, Double units, Double clearing_price);
}
