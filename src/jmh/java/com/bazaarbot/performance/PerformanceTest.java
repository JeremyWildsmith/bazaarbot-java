package com.bazaarbot.performance;

import com.bazaarbot.money.Money;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
@State(Scope.Benchmark)
public class PerformanceTest {

//    private DefaultSimulationStrategy simulation;
//
//    @Setup(Level.Iteration)
//    public void setup() {
//        ICommodity exampleCommodity1 = new ExampleCommodity1();
//        ICommodity exampleCommodity2 = new ExampleCommodity2();
//        ICommodity exampleCommodity3 = new ExampleCommodity3();
//
//
//        List<ICommodity> commodities = List.of(exampleCommodity1, exampleCommodity2, exampleCommodity3);
//
//        simulation = new ExampleAgentSimulation(commodities, new Random());
//    }

    //@Benchmark
//    public void testDuration5s() {
//        IAgent agent1 = new DefaultAgent("TestAgent1", new BigDecimal(20), 10, simulation);
//        IAgent agent2 = new DefaultAgent("TestAgent2", new BigDecimal(40), 5, simulation);
//        IAgent agent3 = new DefaultAgent("TestAgent3", new BigDecimal(60), 20, simulation);
//        Bazaar bazaar = Bazaar.newBuilder()
//                .withDefaultEconomy()
//                .withDefaultMarket()
//                .withDefaultContractResolver()
//                .addAgent(agent1)
//                .addAgent(agent2)
//                .addAgent(agent3)
//                .withDurationBasedRunner(Duration.ofSeconds(5))
//                .build();
//        bazaar.run();
//    }
    private List<Double> doubles1 = new ArrayList<>();
    private List<BigDecimal> decimals1 = new ArrayList<>();
    private List<Double> doubles2 = new ArrayList<>();
    private List<BigDecimal> decimals2 = new ArrayList<>();

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random();
        random.doubles(1_000)
                .forEach((d) -> {
                    decimals1.add(BigDecimal.valueOf(d));
                    doubles1.add(d);
                });
        random.doubles(1_000)
                .forEach((d) -> {
                    decimals2.add(BigDecimal.valueOf(d));
                    doubles2.add(d);
                });
    }

    @Benchmark
    public void addBigDecimal() {
        for (int i = 0; i < decimals1.size(); i++) {
            BigDecimal b1 = decimals1.get(i);
            BigDecimal b2 = decimals2.get(i);

            var d = b1.add(b2).doubleValue();
        }
    }

    @Benchmark
    public void addMoneyMoney() {
        for (int i = 0; i < doubles1.size(); i++) {
            Money m1 = Money.of(doubles1.get(i));
            Money m2 = Money.of(doubles2.get(i));

            var m3 = m1.add(m2).getDoubleAmount();
        }
    }

    @Benchmark
    public void addMoneyBigDecimal() {
        for (int i = 0; i < doubles1.size(); i++) {
            Money m1 = Money.of(doubles1.get(i));

            var m3 = m1.add(decimals1.get(i)).getDoubleAmount();
        }
    }

    @Benchmark
    public void addMoneyDouble() {
        for (int i = 0; i < doubles1.size(); i++) {
            Money m1 = Money.of(doubles1.get(i));
            double m2 = doubles2.get(i);

            var m3 = m1.add(m2).getDoubleAmount();
        }
    }
//
//    @Benchmark
//    public void subtractBigDecimal() {
//        BigDecimal b1 = BigDecimal.valueOf(222_333.9999);
//        BigDecimal b2 = BigDecimal.valueOf(333.9999);
//
//        var d = b1.subtract(b2).doubleValue();
//    }
//
//    @Benchmark
//    public void subtractMoneyMoney() {
//        Money m1 = Money.of(222_333.9999);
//        Money m2 = Money.of(333.9999);
//
//        var m3 = m1.subtract(m2);
//    }
//
//    @Benchmark
//    public void subtractMoneyDouble() {
//        Money m1 = Money.of(222_333.9999);
//        double d1 = 333.9999;
//
//        var m3 = m1.subtract(d1);
//    }
//
//    @Benchmark
//    public void multiplyBigDecimal() {
//        BigDecimal b1 = BigDecimal.valueOf(222_333.9999);
//        BigDecimal b2 = BigDecimal.valueOf(333.9999);
//
//        var d = b1.multiply(b2).doubleValue();
//    }
//
//
//    @Benchmark
//    public void multiplyMoneyMoney() {
//        Money m1 = Money.of(222_333.9999);
//        Money m2 = Money.of(333.9999);
//
//        var m3 = m1.multiply(m2);
//    }
//
//    @Benchmark
//    public void multiplyMoneyDouble() {
//        Money m1 = Money.of(222_333.9999);
//        double d1 = 333.9999;
//
//        var m3 = m1.multiply(d1);
//    }

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
