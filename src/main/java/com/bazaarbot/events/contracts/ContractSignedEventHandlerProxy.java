package com.bazaarbot.events.contracts;

import com.bazaarbot.contract.IContract;
import com.bazaarbot.events.IEventHandler;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author Nick Gritsenko
 */
public final class ContractSignedEventHandlerProxy {

    private final IEventHandler<IContract> handler;

    public ContractSignedEventHandlerProxy(IEventHandler<IContract> handler){
        this.handler = handler;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onContractSigned(ContractSignedEvent event) {
        handler.handle(event.getContract());
    }
}
