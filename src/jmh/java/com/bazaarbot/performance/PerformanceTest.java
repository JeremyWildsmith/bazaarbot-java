package com.bazaarbot.performance;

import com.bazaarbot.Bazaar;
import com.bazaarbot.commodity.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.DefaultSimulationStrategy;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.example.com.bazaarbot.example2.ExampleAgentSimulation;
import com.bazaarbot.example.com.bazaarbot.example2.ExampleCommodity1;
import com.bazaarbot.example.com.bazaarbot.example2.ExampleCommodity2;
import com.bazaarbot.example.com.bazaarbot.example2.ExampleCommodity3;
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

    private DefaultSimulationStrategy simulation;

    @Setup(Level.Iteration)
    public void setup() {
        ICommodity exampleCommodity1 = new ExampleCommodity1();
        ICommodity exampleCommodity2 = new ExampleCommodity2();
        ICommodity exampleCommodity3 = new ExampleCommodity3();


        List<ICommodity> commodities = List.of(exampleCommodity1, exampleCommodity2, exampleCommodity3);

        simulation = new ExampleAgentSimulation(commodities, new Random());
    }

    @Benchmark
    public void testDuration5s() {
        IAgent agent1 = new DefaultAgent("TestAgent1", new BigDecimal(20), 10, simulation);
        IAgent agent2 = new DefaultAgent("TestAgent2", new BigDecimal(40), 5, simulation);
        IAgent agent3 = new DefaultAgent("TestAgent3", new BigDecimal(60), 20, simulation);
        Bazaar bazaar = Bazaar.newBuilder()
                .withDefaultEconomy()
                .withDefaultMarket()
                .withDefaultContractResolver()
                .addAgent(agent1)
                .addAgent(agent2)
                .addAgent(agent3)
                .withDurationBasedRunner(Duration.ofSeconds(5))
                .build();
        bazaar.run();
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
