package com.bazaarbot.example.com.bazaarbot.example2;

import ch.qos.logback.classic.Level;
import com.bazaarbot.Bazaar;
import com.bazaarbot.agent.AgentSimulation;
import com.bazaarbot.economy.DefaultEconomy;
import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.IHistoryRegistryRead;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.CsvMarketReporter;
import com.bazaarbot.market.DefaultMarket;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.runner.TimeBasedRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);


        ICommodity exampleCommodity1 = new ExampleCommodity1();
        ICommodity exampleCommodity2 = new ExampleCommodity2();
        ICommodity exampleCommodity3 = new ExampleCommodity3();


        List<ICommodity> commodities = List.of(exampleCommodity1, exampleCommodity2, exampleCommodity3);

        //DefaultEconomy economy = new DefaultEconomy("DefaultEconomy");
        IAgent agent1 = new DefaultAgent("TestAgent1", new BigDecimal(20), 10);
        IAgent agent2 = new DefaultAgent("TestAgent2", new BigDecimal(40), 5);
        IAgent agent3 = new DefaultAgent("TestAgent3", new BigDecimal(60), 20);
//        agent1.addCommodity(exampleCommodity1, 2.0);
//        agent1.addCommodity(exampleCommodity2, 4.0);
//        agent2.addCommodity(exampleCommodity3, 2.0);
//        agent2.addCommodity(exampleCommodity1, 1.0);
//        agent3.addCommodity(exampleCommodity3, 2.0);
//        agent3.addCommodity(exampleCommodity1, 1.0);
//        agent3.addCommodity(exampleCommodity3, 10.0);
//        economy.addAgent(agent1, new ExampleAgentSimulation(commodities, new Random()));
//        economy.addAgent(agent2, new ExampleAgentSimulation(commodities, new Random()));
//        economy.addAgent(agent3, new ExampleAgentSimulation(commodities, new Random()));
//
//        DefaultMarket market = new DefaultMarket("DefaultMarket");
//        economy.addMarket(market);

        //StepBasedRunner runner = new StepBasedRunner(economy, 100);
        //TimeBasedRunner runner = new TimeBasedRunner(economy, Duration.ofSeconds(10), 500);
//        TimeBasedRunner runner = new TimeBasedRunner(economy, Duration.ofSeconds(5));
//        runner.run();
        AgentSimulation simulation = new ExampleAgentSimulation(commodities, new Random());
        Bazaar bazaar = Bazaar.newBuilder()
                .withDefaultEconomy()
                .withDefaultMarket()
                .addAgent(agent1, simulation)
                .addAgent(agent2, simulation)
                .addAgent(agent3, simulation)
                .withTimeBasedRunner(Duration.ofSeconds(5))
                .build();
        bazaar.run();

//        Statistics statistics = bazaar.getStatistics();
//        for (ICommodity commodity : commodities) {
//            LOG.info("Average historical price for {} is {}", commodity,
//                    statistics.getAverageHistoricalPrice(market, commodity));
//        }
//        IHistoryRegistryRead registry = statistics.getHistoryRegistryByMarket(market);
//        LOG.info("Cheapest commodity: {}", statistics.getCheapestCommodity(market));
//        LOG.info("Dearest commodity: {}", statistics.getDearestGood(market));
//        LOG.info("Hottest commodity: {}", statistics.getHottestCommodity(market));
//        LOG.info("Most profitable agent: {}", statistics.getMostProfitableAgent(market));
//
//        CsvMarketReporter reporter = new CsvMarketReporter(market, statistics);
//        try {
//            reporter.makeBidsReport();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
