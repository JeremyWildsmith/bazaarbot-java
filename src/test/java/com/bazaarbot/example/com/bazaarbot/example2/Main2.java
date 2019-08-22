package com.bazaarbot.example.com.bazaarbot.example2;

import ch.qos.logback.classic.Level;
import com.bazaarbot.Economy2;
import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.inventory.InventoryData;
import com.bazaarbot.market.Market2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public class Main2 {
    private static final Logger LOG = LoggerFactory.getLogger(Main2.class);

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        ICommodity exampleCommodity1 = new ExampleCommodity1();
        ICommodity exampleCommodity2 = new ExampleCommodity2();
        ICommodity exampleCommodity3 = new ExampleCommodity3();

        InventoryData agent1Data = new InventoryData(10, new HashMap<>() {{
            put(exampleCommodity1, 2.0);
        }}, new HashMap<>() {{
            put(exampleCommodity2, 4.0);
        }});

        InventoryData agent2Data = new InventoryData(5, new HashMap<>() {{
            put(exampleCommodity3, 2.0);
            put(exampleCommodity1, 1.0);
        }}, new HashMap<>() {{
            put(exampleCommodity2, 4.0);
            put(exampleCommodity1, 3.0);
        }});

        InventoryData agent3Data = new InventoryData(20, new HashMap<>() {{
            put(exampleCommodity3, 2.0);
            put(exampleCommodity1, 1.0);
            put(exampleCommodity3, 10.0);
        }}, new HashMap<>() {{
            put(exampleCommodity2, 4.0);
            put(exampleCommodity1, 3.0);
            put(exampleCommodity3, 10.0);
        }});

        List<ICommodity> commodities = List.of(exampleCommodity1, exampleCommodity2, exampleCommodity3);

        Economy2 economy = new Economy2();
        IAgent agent1 = new DefaultAgent("TestAgent1", agent1Data, 20);
        IAgent agent2 = new DefaultAgent("TestAgent2", agent2Data, 40);
        IAgent agent3 = new DefaultAgent("TestAgent3", agent3Data, 60);
        economy.addAgent(agent1);
        economy.addAgent(agent2);
        economy.addAgent(agent3);

        economy.addAgentSimulation(agent1, new ExampleAgentSimulation2(commodities, new Random()));
        economy.addAgentSimulation(agent2, new ExampleAgentSimulation2(commodities, new Random()));
        economy.addAgentSimulation(agent3, new ExampleAgentSimulation2(commodities, new Random()));

        Market2 market = new Market2();
        economy.addMarket(market);

        economy.startSimulation(1000);

        Statistics statistics = economy.getStatistics();
        for (ICommodity commodity : commodities) {
            LOG.info("Average historical price for {} is {}", commodity,
                    statistics.getAverageHistoricalPrice(market, commodity));
        }
        LOG.info("Cheapest commodity: {}", statistics.getCheapestCommodity(market));
        LOG.info("Dearest commodity: {}", statistics.getDearestGood(market));
        LOG.info("Hottest commodity: {}", statistics.getHottestCommodity(market));
        LOG.info("Most profitable agent: {}", statistics.getMostProfitableAgent(market));


//        Economy economy = new DoranAndParberryEconomy(new Random(1234));
//        Market market = economy.getMarket("default");
//
//        int rounds = 1;
//        while (true) {
//
//            market.simulate(1);
//            SimpleSummaryMarketReporter reporter = new SimpleSummaryMarketReporter(market.getSnapshot());
//            System.out.println("\nRound: " + rounds);
//            System.out.println(reporter.produce(1));
//
//            try {
//                scanner.nextLine();
//            } catch (NoSuchElementException e) {
//                break;
//            }
//            rounds += 1;
//        }
    }
}
