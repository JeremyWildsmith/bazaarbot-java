package com.bazaarbot.example.com.bazaarbot.example2;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.AgentSimulation2;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
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
    public void simulateActivity(IAgent agent, Market2 market, Statistics statistics) {
        Double d;
        int randomNumber = randomGenerator.nextInt(10) + 1;
        ICommodity commodity1 = commodityList.get(0);
        ICommodity commodity2 = commodityList.get(1);
        ICommodity commodity3 = commodityList.get(2);
        double averagePriceCommodity1 = statistics.getAverageHistoricalPrice(market, commodity1);
        double averagePriceCommodity2 = statistics.getAverageHistoricalPrice(market, commodity2);
        double averagePriceCommodity3 = statistics.getAverageHistoricalPrice(market, commodity3);
        if (randomNumber <= 3) {
            produce(agent, commodity1, randomNumber);
            if(agent.queryInventory(commodity1) > 0) {
                market.putBid(new Offer(agent, commodity1, agent.queryInventory(commodity1) / randomNumber + 1, 10));
            }
            if (agent.queryInventory(commodity3) > 0) {
                market.putBid(new Offer(agent, commodity3, agent.queryInventory(commodity3) / randomNumber + 1, randomNumber + 3));
            }
        } else if (randomNumber <= 6) {
            produce(agent, commodity1, randomNumber);
            consume(agent, commodity2, randomNumber - 1);
            produce(agent, commodity3, randomNumber + 50, 0.4);
            if (agent.queryInventory(commodity1) > 0) {
                market.putBid(new Offer(agent, commodity1, agent.queryInventory(commodity1) / randomNumber + 1, 2));
            }
            market.putAsk(new Offer(agent, commodity2, (averagePriceCommodity2 + 1) / randomNumber + randomGenerator.nextInt(2), randomNumber + 2));
        } else if (randomNumber <= 8) {
            produce(agent, commodity2, randomNumber);
            consume(agent, commodity1, randomNumber);
            produce(agent, commodity1, randomNumber + 50, 0.4);
            market.putAsk(new Offer(agent, commodity1, (averagePriceCommodity1 + 1) /  randomNumber + randomGenerator.nextInt(2), randomNumber - 3));
            if (agent.queryInventory(commodity1) > 0) {
                market.putBid(new Offer(agent, commodity1, agent.queryInventory(commodity1) / randomNumber + 2, 3));
            }
        } else {
            consume(agent, commodity3, randomNumber);
            produce(agent, commodity1, randomNumber);
            produce(agent, commodity2, randomNumber + 50, 0.4);
            if (agent.queryInventory(commodity3) > 0) {
                market.putBid(new Offer(agent, commodity3, agent.queryInventory(commodity3) / randomNumber + 2, randomNumber));
            }
            if (agent.queryInventory(commodity2) > 0) {
                market.putBid(new Offer(agent, commodity2, agent.queryInventory(commodity2) / randomNumber + 2, 3));
            }
            market.putAsk(new Offer(agent, commodity3, (averagePriceCommodity3 + 1)/ randomNumber + randomGenerator.nextInt(2), randomNumber + 3));
        }

    }
}
