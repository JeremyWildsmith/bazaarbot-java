package com.bazaarbot.performance;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.AgentSimulation;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.Statistics;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.Offer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * @author Nick Gritsenko
 */
public class ExampleAgentSimulation extends AgentSimulation {
    private final Random randomGenerator;
    private final List<ICommodity> commodityList;

    public ExampleAgentSimulation(List<ICommodity> commodityList, Random randomGenerator) {
        super(randomGenerator);
        this.randomGenerator = randomGenerator;
        this.commodityList = commodityList;
    }

    @Override
    public void simulateActivity(IAgent agent, Market market, Statistics statistics) {
        Double d;
        int randomNumber = randomGenerator.nextInt(10) + 1;
        ICommodity commodity1 = commodityList.get(0);
        ICommodity commodity2 = commodityList.get(1);
        ICommodity commodity3 = commodityList.get(2);
        BigDecimal averagePriceCommodity1 = statistics.getAverageHistoricalPrice(market, commodity1);
        BigDecimal averagePriceCommodity2 = statistics.getAverageHistoricalPrice(market, commodity2);
        BigDecimal averagePriceCommodity3 = statistics.getAverageHistoricalPrice(market, commodity3);
        if (randomNumber <= 3) {
            produce(agent, commodity1, randomNumber);
            if(agent.getCommodityAmount(commodity1) > 0) {
                market.putBid(new Offer(agent, commodity1, agent.getCommodityAmount(commodity1) / randomNumber + 1, new BigDecimal(10)));
            }
            if (agent.getCommodityAmount(commodity3) > 0) {
                market.putBid(new Offer(agent, commodity3, agent.getCommodityAmount(commodity3) / randomNumber + 1, new BigDecimal(randomNumber + 3)));
            }
        } else if (randomNumber <= 6) {
            produce(agent, commodity1, randomNumber);
            consume(agent, commodity2, randomNumber - 1);
            produce(agent, commodity3, randomNumber + 50, 0.4);
            if (agent.getCommodityAmount(commodity1) > 0) {
                market.putBid(new Offer(agent, commodity1, agent.getCommodityAmount(commodity1) / randomNumber + 1, new BigDecimal(2)));
            }
            market.putAsk(new Offer(agent, commodity2,
                    (averagePriceCommodity2.doubleValue() + 1) / randomNumber + randomGenerator.nextInt(2),
                    new BigDecimal(randomNumber + 2)));
        } else if (randomNumber <= 8) {
            produce(agent, commodity2, randomNumber);
            consume(agent, commodity1, randomNumber);
            produce(agent, commodity1, randomNumber + 50, 0.4);
            market.putAsk(new Offer(agent, commodity1,
                    (averagePriceCommodity1.doubleValue() + 1) /  randomNumber + randomGenerator.nextInt(2),
                    new BigDecimal(randomNumber - 3)));
            if (agent.getCommodityAmount(commodity1) > 0) {
                market.putBid(new Offer(agent, commodity1, agent.getCommodityAmount(commodity1) / randomNumber + 2,
                        new BigDecimal(3)));
            }
        } else {
            consume(agent, commodity3, randomNumber);
            produce(agent, commodity1, randomNumber);
            produce(agent, commodity2, randomNumber + 50, 0.4);
            if (agent.getCommodityAmount(commodity3) > 0) {
                market.putBid(new Offer(agent, commodity3,
                        agent.getCommodityAmount(commodity3) / randomNumber + 2,
                        new BigDecimal(randomNumber)));
            }
            if (agent.getCommodityAmount(commodity2) > 0) {
                market.putBid(new Offer(agent, commodity2,
                        agent.getCommodityAmount(commodity2) / randomNumber + 2,
                        new BigDecimal(3)));
            }
            market.putAsk(new Offer(agent, commodity3,
                    (averagePriceCommodity3.doubleValue() + 1)/ randomNumber + randomGenerator.nextInt(2),
                    new BigDecimal(randomNumber + 3)));
        }

    }
}
