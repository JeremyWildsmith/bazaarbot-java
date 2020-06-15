package com.bazaarbot.events;

import com.bazaarbot.contract.IContract;

public class ContractSignedEvent implements IEvent {

    private IContract contract;

    public ContractSignedEvent(IContract contract) {
        this.contract = contract;
    }

    public IContract getContract() {
        return contract;
    }
}
