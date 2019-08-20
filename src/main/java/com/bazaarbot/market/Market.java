//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.TradeBook;
import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.History;

import java.util.*;

public class Market {
    private final String name;
    /**
     * Logs information about all economic activity in this market
     **/
    private History history;
    /**
     * Signal fired when an agent's money reaches 0 or below
     **/
    private ISignalBankrupt signalBankrupt;
    /********PRIVATE*********/
    private int roundNum = 0;
    private List<ICommodity> goodTypes;
    //list of string ids for all the legal commodities
    private List<IAgent> agents;
    private TradeBook tradeBook;

    private final IOfferResolver offerResolver;
    private final IOfferExecuter offerExecutor;
    private final Random rng;

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IOfferResolver offerResolver, IOfferExecuter executor) {
        this(name, marketData, isb, offerResolver, executor, new Random());
    }

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IOfferResolver offerResolver, IOfferExecuter executor, Random rng) {
        this.name = name;
        this.history = new History();
        this.tradeBook = new TradeBook();
        this.goodTypes = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.signalBankrupt = isb;
        this.offerResolver = offerResolver;
        this.offerExecutor = executor;
        this.rng = rng;
        fromData(marketData);
    }

    public void replaceAgent(IAgent oldAgent, IAgent newAgent) {
        agents.set(agents.indexOf(oldAgent), newAgent);
    }

    public String getName() {
        return name;
    }

    //@:access(bazaarbot.agent.IAgent)    //dfs stub ????
    public void simulate(int rounds) {
        for (int round = 0; round < rounds; round++) {
            for (IAgent agent : agents) {
                agent.simulate(this);
                for (ICommodity commodity : goodTypes) {
                    agent.generateOffers(this, commodity);
                }
            }

            resolveOffers();

            List<IAgent> del = new ArrayList<>();
            for (IAgent agent : agents) {
                if (agent.getMoneyAvailable() <= 0)
                    del.add(agent);

            }
            while (del.size() > 0) {
                signalBankrupt.signalBankrupt(this, del.get(0));
                //signalBankrupt.dispatch(this, agent);
                del.remove(0);
            }
            roundNum++;
        }
    }

    public void ask(Offer offer) {
        tradeBook.ask(offer);
    }

    public void bid(Offer offer) {
        tradeBook.bid(offer);
    }

    /**
     * Returns the historical mean price of the given commodity over the last X rounds
     *
     * @param good  string id of commodity
     * @param range number of rounds to look back
     * @return
     */
    public double getAverageHistoricalPrice(ICommodity good, int range) {
        return history.getPrices().average(good, range);
    }

    /**
     * Get the good with the highest demand/supply ratio over time
     *
     * @param minimum the minimum demand/supply ratio to consider an opportunity
     * @param range   number of rounds to look back
     * @return
     */
    public ICommodity getHottestGood(double minimum, int range) {
        ICommodity bestMarket = null;

        double bestRatio = -99999;
        for (ICommodity good : goodTypes) {
            // Math.NEGATIVE_INFINITY;
            Double asks = history.getAsks().average(good, range);
            Double bids = history.getBids().average(good, range);
            double ratio = 0;
            if (asks == 0 && bids > 0) {
                //If there are NONE on the market we artificially create a fake supply of 1/2 a unit to avoid the
                //crazy bias that "infinite" demand can cause...
                asks = 0.5;
            }

            ratio = bids / asks;
            if (ratio > minimum && ratio > bestRatio) {
                bestRatio = ratio;
                bestMarket = good;
            }

        }

        return bestMarket;
    }


    public ICommodity getHottestGood() {
        return getHottestGood(1.5, 10);
    }

    /**
     * Returns the good that has the lowest average price over the given range of time
     *
     * @param range   how many rounds to look back
     * @param exclude goods to exclude
     * @return
     */
    public ICommodity getCheapestGood(int range, List<ICommodity> exclude) {
        double bestPrice = -9999999;
        // Math.POSITIVE_INFINITY;
        ICommodity bestGood = null;
        for (ICommodity g : goodTypes) {
            if (exclude == null || !exclude.contains(g)) {
                double price = history.getPrices().average(g, range);
                if (price < bestPrice) {
                    bestPrice = price;
                    bestGood = g;
                }

            }

        }

        return bestGood;
    }

    /**
     * Returns the good that has the highest average price over the given range of time
     *
     * @param range   how many rounds to look back
     * @param exclude goods to exclude
     * @return
     */
    public ICommodity getDearestGood(int range, List<ICommodity> exclude) {
        double bestPrice = 0;
        ICommodity bestGood = null;
        for (ICommodity g : goodTypes) {
            if (exclude == null || !exclude.contains(g)) {
                Double price = history.getPrices().average(g, range);
                if (price > bestPrice) {
                    bestPrice = price;
                    bestGood = g;
                }

            }

        }
        return bestGood;
    }

    public List<String> getAgentClassNames() {
        HashSet<String> classes = new HashSet<>();

        for (IAgent a : agents)
            classes.add(a.getAgentName());

        return new ArrayList<>(classes);
    }

    /**
     * @param range
     * @return
     */
    public String getMostProfitableAgentClass(int range) {
        double best = -999999;
        // Math.NEGATIVE_INFINITY;
        String bestClass = "";
        for (String className : getAgentClassNames()) {
            double val = history.getProfit().average(className, range);
            if (val > best) {
                bestClass = className;
                best = val;
            }

        }
        return bestClass;
    }

    public final String getMostProfitableAgentClass() {
        return getMostProfitableAgentClass(10);
    }

    public MarketSnapshot getSnapshot() {
        List<AgentSnapshot> agentData = new ArrayList<>();
        for (IAgent a : agents) {
            agentData.add(a.getSnapshot());
        }

        return new MarketSnapshot(new History(history), agentData);
    }

    /********PRIVATE*********/
    private void fromData(MarketData data) {
        for (ICommodity g : data.goods) {
            //Create commodity index
            goodTypes.add(g);

            double v = 1.0;
            if (g.getName().compareTo("metal") == 0)
                v = 2.0;

            if (g.getName().compareTo("tools") == 0)
                v = 3.0;

            history.registerCommodity(g);
            history.getPrices().add(g, v);
            //start the bidding at $1!
            history.getAsks().add(g, v);
            //start history charts with 1 fake buy/sell bid
            history.getBids().add(g, v);
            history.getTrades().add(g, v);
            tradeBook.register(g);
        }

        //Make the agent list
        agents = new ArrayList<>();
        agents.addAll(data.agents);
    }

    private static double listAvgf(List<Double> list) {
        double avg = 0;
        for (int j = 0; j < list.size(); j++) {
            avg += list.get(j);
        }
        avg /= list.size();
        return avg;
    }

    private void resolveOffers() {
        for (Map.Entry<ICommodity, List<Offer>> asks : tradeBook.getAsks().entrySet()) {
            double count = 0;

            for (Offer o : asks.getValue())
                count += o.getUnits();

            history.getAsks().add(asks.getKey(), count);

        }

        for (Map.Entry<ICommodity, List<Offer>> bids : tradeBook.getBids().entrySet()) {
            double count = 0;

            for (Offer o : bids.getValue())
                count += o.getUnits();

            history.getBids().add(bids.getKey(), count);
        }

        Map<ICommodity, OfferResolutionStatistics> r = offerResolver.resolve(offerExecutor,
                new HashMap<>(tradeBook.getBids()), new HashMap<>(tradeBook.getAsks()));
        tradeBook.getBids().clear();
        tradeBook.getAsks().clear();

        for (Map.Entry<ICommodity, OfferResolutionStatistics> e : r.entrySet()) {
            OfferResolutionStatistics stats = e.getValue();
            history.getTrades().add(e.getKey(), stats.getUnitsTraded());

            if (stats.getUnitsTraded() > 0) {
                history.getPrices().add(e.getKey(), stats.getMoneyTraded() / stats.getUnitsTraded());
            } else {
                //special case: none were traded this round, use last round's average price
                history.getPrices().add(e.getKey(), history.getPrices().average(e.getKey(), 1));
            }
        }
        List<IAgent> ag = new ArrayList<>(agents);//.<IAgent>ToList();
        ag.sort(Comparator.comparing(IAgent::getAgentName));
        String curr_class = "";
        String last_class = "";
        List<Double> list = null;

        for (int i = 0; i < ag.size(); i++) {
            IAgent a = ag.get(i);
            //get current agent
            curr_class = a.getAgentName();
            //check its class
            if (curr_class.compareTo(last_class) != 0) {
                //new class?
                if (list != null) {
                    //do we have a list built up?
                    //log last class' profit
                    history.getProfit().add(last_class, listAvgf(list));
                }

                list = new ArrayList<Double>();
                //make a new list
                last_class = curr_class;
            }

            list.add(a.getLastSimulateProfit());
        }

        //push profit onto list
        //add the last class too
        if (list != null)
            history.getProfit().add(last_class, listAvgf(list));
    }
}


