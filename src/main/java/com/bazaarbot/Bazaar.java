package com.bazaarbot;

import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.DefaultContractResolver;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.economy.DefaultEconomy;
import com.bazaarbot.economy.IEconomy;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.runner.DurationBasedRunner;
import com.bazaarbot.runner.FixedRateBasedRunner;
import com.bazaarbot.runner.IRunner;
import com.bazaarbot.runner.RoundBasedRunner;

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

    private final ExecutorService executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
        private int threadCounter = 0;
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Bazaar-EventBus-"+(++threadCounter));
        }
    });

    private Bazaar() {
//        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
//        root.setLevel(Level.DEBUG);
    }

    public void run() {
        if (economy == null) {
            throw new RuntimeException("Economy is null, initiate Economy object first!");
        }
        runner.run();
        executorService.shutdownNow();
    }

    public void runAsync() {
        if (economy == null) {
            throw new RuntimeException("Economy is null, initiate Economy object first!");
        }
        runner.runAsync();
        //executorService.shutdownNow();
    }

    public IEconomy getEconomy() {
        if (economy == null) {
            throw new RuntimeException("Economy is null, initiate Economy object first!");
        }
        return economy;
    }

    public static BazaarBuilder newBuilder() {
        return new Bazaar().new BazaarBuilder();
    }

    public class BazaarBuilder {

        private BazaarBuilder() {
            // private constructor
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
            Bazaar.this.economy.setContractResolver(resolver);
            return this;
        }

        public BazaarBuilder setContractResolver(IContractResolver resolver) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.economy.setContractResolver(resolver);
            return this;
        }

        public BazaarBuilder setRunner(IRunner runner) {
            Bazaar.this.runner = runner;
            return this;
        }

        public BazaarBuilder withRoundBasedRunner(int rounds) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.runner = new RoundBasedRunner(economy, rounds);
            return this;
        }

        public BazaarBuilder withDurationBasedRunner(Duration duration) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.runner = new DurationBasedRunner(economy, duration);
            return this;
        }

        public BazaarBuilder withFixedRateBasedRunner(Duration fixedRate) {
            if (economy == null) {
                throw new RuntimeException("Economy is null, initiate Economy object first!");
            }
            Bazaar.this.runner = new FixedRateBasedRunner(economy, fixedRate);
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
