package com.bazaarbot.agent;

import com.bazaarbot.ICommodity;

import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public class AgentSimulationExample2 extends AgentSimulation2 {
    public AgentSimulationExample2(IAgent agent, Random randomGenerator) {
        super(agent, randomGenerator);
    }

    public class A1 implements ICommodity {

        @Override
        public String getName() {
            return "Test";
        }

        @Override
        public double getSpace() {
            return 1;
        }
    }

    @Override
    public void simulateActivity() {
        agent.addInventoryItem(new A1(),2);
    }
}
