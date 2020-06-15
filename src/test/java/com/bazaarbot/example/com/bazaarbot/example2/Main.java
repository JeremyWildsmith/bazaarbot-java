package com.bazaarbot.example.com.bazaarbot.example2;

import ch.qos.logback.classic.Level;
import com.bazaarbot.Bazaar;
import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.DefaultSimulationStrategy;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.contract.IContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

//    private static class ContractSignedEventHandler implements IEventHandler<IContract> {
//
//        @Override
//        public void handle(IContract contract) {
//            LOG.info("Contract signed!");
//            LOG.info("{}", contract);
//        }
//    }

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);


        ICommodity exampleCommodity1 = new ExampleCommodity1();
        ICommodity exampleCommodity2 = new ExampleCommodity2();
        ICommodity exampleCommodity3 = new ExampleCommodity3();


        List<ICommodity> commodities = List.of(exampleCommodity1, exampleCommodity2, exampleCommodity3);

        DefaultSimulationStrategy simulation = new ExampleAgentSimulation(commodities, new Random());

        IAgent agent1 = new DefaultAgent("TestAgent1", new BigDecimal(20), 10, simulation);
        IAgent agent2 = new DefaultAgent("TestAgent2", new BigDecimal(40), 5, simulation);
        IAgent agent3 = new DefaultAgent("TestAgent3", new BigDecimal(60), 20, simulation);

        Bazaar bazaar = Bazaar.newBuilder()
                .withDefaultEconomy()
                .withDefaultMarket()
                .withDefaultContractResolver()
                //.withContractEvents(new ContractSignedEventHandler())
                .addAgent(agent1)
                .addAgent(agent2)
                .addAgent(agent3)
                .withDurationBasedRunner(Duration.ofSeconds(5))
                .build();
        bazaar.runAsync();
    }
}
