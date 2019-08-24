package com.bazaarbot;

import com.bazaarbot.agent.AgentSimulation;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.economy.DefaultEconomy;
import com.bazaarbot.economy.IEconomy;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.runner.IRunner;
import com.bazaarbot.runner.StepBasedRunner;
import com.bazaarbot.runner.TimeBasedRunner;

import java.time.Duration;

/**
 * @author Nick Gritsenko
 */
public class Bazaar {
    private IRunner runner;
    private IEconomy economy;

    private Bazaar(){}

    public void run() {
        runner.run();
    }

    public Statistics getStatistics() {
        return economy.getStatistics();
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

        public BazaarBuilder setRunner(IRunner runner) {
            Bazaar.this.runner = runner;
            return this;
        }
        public BazaarBuilder withStepBasedRunner(int steps) {
            Bazaar.this.runner = new StepBasedRunner(economy, steps);
            return this;
        }
        public BazaarBuilder withTimerBasedRunner(Duration duration, long ticks) {
            Bazaar.this.runner = new TimeBasedRunner(economy, duration, ticks);
            return this;
        }
        public BazaarBuilder withTimeBasedRunner(Duration duration) {
            Bazaar.this.runner = new TimeBasedRunner(economy, duration, 0);
            return this;
        }
        public BazaarBuilder setMarket(IMarket market) {
            Bazaar.this.economy.addMarket(market);
            return this;
        }
        public BazaarBuilder withDefaultMarket() {
            Bazaar.this.economy.addMarket(new DefaultMarket());
            return this;
        }
        public BazaarBuilder withDefaultMarket(String name) {
            Bazaar.this.economy.addMarket(new DefaultMarket(name));
            return this;
        }
        public BazaarBuilder addAgent(IAgent agent, AgentSimulation agentSimulation) {
            Bazaar.this.economy.addAgent(agent, agentSimulation);
            return this;
        }
        public BazaarBuilder addAgent(IMarket market, IAgent agent, AgentSimulation agentSimulation) {
            Bazaar.this.economy.addAgent(market, agent, agentSimulation);
            return this;
        }

        public Bazaar build() {
            return Bazaar.this;
        }

    }
}
