package com.bazaarbot.example.com.bazaarbot.example2;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.AgentSimulation2;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.market.Market2;
import com.bazaarbot.market.Offer;

import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public class ExampleAgentSimulation2 extends AgentSimulation2 {
    private final Random randomGenerator;
    private final List<ICommodity> commodityList;

    public ExampleAgentSimulation2(List<ICommodity> commodityList, Random randomGenerator) {
        super(randomGenerator);
        this.randomGenerator = randomGenerator;
        this.commodityList = commodityList;
    }

    @Override
    public void simulateActivity(IAgent agent, Market2 market) {
        int randomNumber = randomGenerator.nextInt(10);

        if (randomNumber < 3) {
            produce(agent, commodityList.get(0), randomNumber);
            market.putBid(new Offer(agent, commodityList.get(0), randomNumber, 1));
        } else if (randomNumber < 6) {
            produce(agent, commodityList.get(0), randomNumber, 0.2);
            consume(agent, commodityList.get(1), randomNumber - 1);
            market.putBid(new Offer(agent, commodityList.get(0), randomNumber - 1, 2));
            market.putAsk(new Offer(agent, commodityList.get(1), randomNumber, 3));
        } else if (randomNumber < 8) {
            produce(agent, commodityList.get(1), randomNumber, 0.2);
            consume(agent, commodityList.get(0), randomNumber, 0.4);
            market.putBid(new Offer(agent, commodityList.get(1), randomNumber - 1, 1));
            market.putAsk(new Offer(agent, commodityList.get(0), randomNumber - 1, 3));
        } else {
            consume(agent, commodityList.get(2), randomNumber);
            produce(agent, commodityList.get(0), randomNumber, 0.2);
            market.putBid(new Offer(agent, commodityList.get(0), randomNumber - 2, 1));
            market.putAsk(new Offer(agent, commodityList.get(2), randomNumber - 1, 2));
        }

    }
}
