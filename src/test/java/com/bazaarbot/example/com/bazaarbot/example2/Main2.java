package com.bazaarbot.example.com.bazaarbot.example2;

import com.bazaarbot.Economy2;
import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.inventory.InventoryData;
import com.bazaarbot.market.*;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Nick Gritsenko
 */
public class Main2 {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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

        economy.addMarket(new Market2(
                new MarketData(
                        commodities,
                        List.of(agent1, agent2, agent3)
                ), (m, agent) -> System.out.println(agent.getAgentName() + " is bankrupt!"),
                new DefaultOfferResolver(),
                new DefaultOfferExecutor()
        ));

        economy.startSimulation(3_000_000);

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
