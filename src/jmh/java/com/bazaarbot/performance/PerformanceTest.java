package com.bazaarbot.performance;

import com.bazaarbot.Economy;
import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.market.Market;
import com.bazaarbot.simulation.TimeBasedRunner;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
@State(Scope.Benchmark)
public class PerformanceTest {

    private TimeBasedRunner runner;


    @Setup(Level.Iteration)
    public void setup() {
        ICommodity exampleCommodity1 = new ExampleCommodity1();
        ICommodity exampleCommodity2 = new ExampleCommodity2();
        ICommodity exampleCommodity3 = new ExampleCommodity3();


        List<ICommodity> commodities = List.of(exampleCommodity1, exampleCommodity2, exampleCommodity3);

        Economy economy = new Economy("Market");
        IAgent agent1 = new DefaultAgent("TestAgent1", new BigDecimal(20), 10);
        IAgent agent2 = new DefaultAgent("TestAgent2", new BigDecimal(40), 5);
        IAgent agent3 = new DefaultAgent("TestAgent3", new BigDecimal(60), 20);
        agent1.addCommodity(exampleCommodity1, 2.0);
        agent1.addCommodity(exampleCommodity2, 4.0);
        agent2.addCommodity(exampleCommodity3, 2.0);
        agent2.addCommodity(exampleCommodity1, 1.0);
        agent3.addCommodity(exampleCommodity3, 2.0);
        agent3.addCommodity(exampleCommodity1, 1.0);
        agent3.addCommodity(exampleCommodity3, 10.0);
        economy.addAgent(agent1);
        economy.addAgent(agent2);
        economy.addAgent(agent3);

        economy.addAgentSimulation(agent1, new ExampleAgentSimulation(commodities, new Random()));
        economy.addAgentSimulation(agent2, new ExampleAgentSimulation(commodities, new Random()));
        economy.addAgentSimulation(agent3, new ExampleAgentSimulation(commodities, new Random()));

        Market market = new Market();
        economy.addMarket(market);

        //StepBasedRunner runner = new StepBasedRunner(economy, 5000);
        runner = new TimeBasedRunner(economy, Duration.ofSeconds(5), 500);
        //TimeBasedRunner runner = new TimeBasedRunner(economy, Duration.ofSeconds(5));
        runner.run();
    }

    @Benchmark
    public void test10() {
        runner.run();
    }

//    @Benchmark
//    public void test100() {
//        economy.startSimulation(100);
//    }
//
//    @Benchmark
//    public void test1000() {
//        economy.startSimulation(1000);
//    }
//
//    @Benchmark
//    public void test10000() {
//        economy.startSimulation(10_000);
//    }
}
