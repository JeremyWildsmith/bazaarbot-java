package com.bazaarbot.events.contracts;

import com.bazaarbot.contract.IContract;

/**
 * @author Nick Gritsenko
 */
public final class ContractSignedEvent {

    private final IContract contract;

    public ContractSignedEvent(IContract contract) {
        this.contract = contract;
    }

    public IContract getContract() {
        return contract;
    }
}
