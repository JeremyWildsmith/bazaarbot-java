package com.bazaarbot.performance;

import com.bazaarbot.Economy2;
import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.inventory.InventoryData;
import com.bazaarbot.market.Market2;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Nick Gritsenko
 */
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
public class PerformanceTest {

    private Economy2 economy;


    @Setup(Level.Iteration)
    public void setup() {
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

        this.economy = new Economy2();
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
    }

    @Benchmark
    public void test10() {
        economy.startSimulation(10);
    }

    @Benchmark
    public void test100() {
        economy.startSimulation(100);
    }

    @Benchmark
    public void test1000() {
        economy.startSimulation(1000);
    }

    @Benchmark
    public void test10000() {
        economy.startSimulation(10_000);
    }
}
