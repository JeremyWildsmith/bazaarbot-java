package com.bazaarbot;

import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.DefaultContractResolver;
import com.bazaarbot.contract.IContract;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.economy.DefaultEconomy;
import com.bazaarbot.economy.IEconomy;
import com.bazaarbot.events.contracts.ContractSignedEventHandlerProxy;
import com.bazaarbot.events.IEventHandler;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.runner.IRunner;
import com.bazaarbot.runner.StepBasedRunner;
import com.bazaarbot.runner.TimeBasedRunner;
import org.greenrobot.eventbus.EventBus;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Nick Gritsenko
 */
public class Bazaar {
    private IRunner runner;
    private IEconomy economy;
    private EventBus userEventBus;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
        private int threadCounter = 0;
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Bazaar-EventBus-"+(++threadCounter));
        }
    });

    private Bazaar() {
    }

    public void run() {
        if (economy == null) {
            throw new RuntimeException("Economy is null, initiate Economy object first!");
        }
        runner.run();
        executorService.shutdownNow();
    }

    public Statistics getStatistics() {
        if (economy == null) {
            throw new RuntimeException("Economy is null, initiate Economy object first!");
        }
        return economy.getStatistics();
    }

    public static BazaarBuilder newBuilder() {
        return new Bazaar().new BazaarBuilder();
    }

    public class BazaarBuilder {

        private BazaarBuilder() {
            // private constructor
        }

        public BazaarBuilder withContractEvents(IEventHandler<IContract> userEvent) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.userEventBus = EventBus.builder().executorService(executorService).build();
            Bazaar.this.economy.setUserEventBus(userEventBus);
            Bazaar.this.userEventBus.register(new ContractSignedEventHandlerProxy(userEvent));
            return this;
        }

        public BazaarBuilder setEconomy(IEconomy economy) {
            Bazaar.this.economy = economy;
            return this;
        }

        public BazaarBuilder withDefaultEconomy() {
            Bazaar.this.economy = new DefaultEconomy();
            return this;
        }

        public BazaarBuilder withDefaultContractResolver() {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            IContractResolver resolver = new DefaultContractResolver();
            resolver.setStatistics(economy.getStatistics());
            Bazaar.this.economy.setContractResolver(resolver);
            return this;
        }

        public BazaarBuilder setContractResolver(IContractResolver resolver) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            resolver.setStatistics(economy.getStatistics());
            Bazaar.this.economy.setContractResolver(resolver);
            return this;
        }

        public BazaarBuilder setRunner(IRunner runner) {
            Bazaar.this.runner = runner;
            return this;
        }

        public BazaarBuilder withStepBasedRunner(int steps) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.runner = new StepBasedRunner(economy, steps);
            return this;
        }

        public BazaarBuilder withTimerBasedRunner(Duration duration, long ticks) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.runner = new TimeBasedRunner(economy, duration, ticks);
            return this;
        }

        public BazaarBuilder withTimeBasedRunner(Duration duration) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.runner = new TimeBasedRunner(economy, duration, 0);
            return this;
        }

        public BazaarBuilder setMarket(IMarket market) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.economy.addMarket(market);
            return this;
        }

        public BazaarBuilder withDefaultMarket() {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.economy.addMarket(new DefaultMarket());
            return this;
        }

        public BazaarBuilder withDefaultMarket(String name) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.economy.addMarket(new DefaultMarket(name));
            return this;
        }

        public BazaarBuilder addAgent(IAgent agent) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.economy.addAgent(agent);
            return this;
        }

        public Bazaar build() {
            return Bazaar.this;
        }

    }
}
