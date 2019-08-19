//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.*;
import com.bazaarbot.agent.AgentData;
import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.agent.IAgent;
import com.bazaarbot.history.History;

import java.util.*;

public class Market   
{
    public String name;
    /**Logs information about all economic activity in this market**/
    public History history;
    /**Signal fired when an agent's money reaches 0 or below**/
    public ISignalBankrupt signalBankrupt;
    /********PRIVATE*********/
    private int _roundNum = 0;
    private List<ICommodity> _goodTypes;
    //list of string ids for all the legal commodities
    public List<IAgent> _agents = new ArrayList<>();
    public TradeBook _book;
    private HashMap<String, AgentData> _mapAgents;

    private final IOfferResolver offerResolver;
    private final IOfferExecuter offerExecutor;
    private final Random rng;

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IOfferResolver offerResolver, IOfferExecuter executor) {
        this(name, marketData, isb, offerResolver, executor, new Random());
    }

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IOfferResolver offerResolver, IOfferExecuter executor, Random rng) {
        this.name = name;
        history = new History();
        _book = new TradeBook();
        _goodTypes = new ArrayList<>();
        _agents = new ArrayList<>();
        _mapAgents = new HashMap<>();
        signalBankrupt = isb;
        this.offerResolver = offerResolver;
        this.offerExecutor = executor;
        this.rng = rng;
        fromData(marketData);
    }

    public void replaceAgent(IAgent oldAgent, IAgent newAgent) {
        _agents.remove(oldAgent);
        _agents.add(newAgent);
    }

    //@:access(bazaarbot.agent.IAgent)    //dfs stub ????
    public void simulate(int rounds) {
        for (int round = 0;round < rounds;round++)
        {
            for (IAgent agent : _agents)
            {
                agent.setMoneyLastRound(agent.getMoney());
                agent.simulate(this);
                for (ICommodity commodity : _goodTypes)
                {
                    agent.generateOffers(this, commodity);
                }
            }

            resolveOffers();

            List<IAgent> del = new ArrayList<>();
            for (IAgent agent : _agents)
            {
                if (agent.getMoney() <= 0)
                    del.add(agent);
                 
            }
            while (del.size() > 0)
            {
                signalBankrupt.signalBankrupt(this,del.get(0));
                //signalBankrupt.dispatch(this, agent);
                del.remove(0);
            }
            _roundNum++;
        }
    }

    public void ask(Offer offer) {
        _book.ask(offer);
    }

    public void bid(Offer offer) {
        _book.bid(offer);
    }

    /**
     * Returns the historical mean price of the given commodity over the last X rounds
     * @param	good string id of commodity
     * @param	range number of rounds to look back
     * @return
     */
    public double getAverageHistoricalPrice(ICommodity good, int range) {
        return history.prices.average(good,range);
    }

    /**
     * Get the good with the highest demand/supply ratio over time
     * @param   minimum the minimum demand/supply ratio to consider an opportunity
     * @param	range number of rounds to look back
     * @return
     */
    public ICommodity getHottestGood(double minimum, int range) {
        ICommodity best_market = null;

        double best_ratio = -99999;
        for (ICommodity good : _goodTypes)
        {
            // Math.NEGATIVE_INFINITY;
            Double asks = history.asks.average(good,range);
            Double bids = history.bids.average(good,range);
            double ratio = 0;
            if (asks == 0 && bids > 0)
            {
                //If there are NONE on the market we artificially create a fake supply of 1/2 a unit to avoid the
                //crazy bias that "infinite" demand can cause...
                asks = 0.5;
            }
             
            ratio = bids / asks;
            if (ratio > minimum && ratio > best_ratio)
            {
                best_ratio = ratio;
                best_market = good;
            }
             
        }

        return best_market;
    }


    public ICommodity getHottestGood() {
        return getHottestGood(1.5, 10);
    }

    /**
    	     * Returns the good that has the lowest average price over the given range of time
    	     * @param	range how many rounds to look back
    	     * @param	exclude goods to exclude
    	     * @return
    	     */
    public ICommodity getCheapestGood(int range, List<ICommodity> exclude) {
        double best_price = -9999999;
        // Math.POSITIVE_INFINITY;
        ICommodity best_good = null;
        for (ICommodity g : _goodTypes)
        {
            if (exclude == null || !exclude.contains(g))
            {
                double price = history.prices.average(g,range);
                if (price < best_price)
                {
                    best_price = price;
                    best_good = g;
                }
                 
            }
             
        }

        return best_good;
    }

    /**
     * Returns the good that has the highest average price over the given range of time
     * @param	range how many rounds to look back
     * @param	exclude goods to exclude
     * @return
     */
    public ICommodity getDearestGood(int range, List<ICommodity> exclude) {
        double best_price = 0;
        ICommodity best_good = null;
        for (ICommodity g : _goodTypes)
        {
            if (exclude == null || !exclude.contains(g))
            {
                Double price = history.prices.average(g,range);
                if (price > best_price)
                {
                    best_price = price;
                    best_good = g;
                }
                 
            }
             
        }
        return best_good;
    }

    /**
     *
     * @param	range
     * @return
     */
    public String getMostProfitableAgentClass(int range) {
        double best = -999999;
        // Math.NEGATIVE_INFINITY;
        String bestClass = "";
        for (String className : _mapAgents.keySet())
        {
            double val = history.profit.average(className, range);
            if (val > best)
            {
                bestClass = className;
                best = val;
            }
             
        }
        return bestClass;
    }

    public final String getMostProfitableAgentClass() {
        return getMostProfitableAgentClass(10);
    }

    public AgentData getAgentClass(String className) {
        return _mapAgents.get(className);
    }

    public List<String> getAgentClassNames() {
        return new ArrayList<>(_mapAgents.keySet());
    }

    public MarketSnapshot getSnapshot() {
        List<AgentSnapshot> agentData = new ArrayList<>();
        for(IAgent a : _agents) {
            agentData.add(a.getSnapshot());
        }

        return new MarketSnapshot(new History(history), agentData);
    }

    /********PRIVATE*********/
    private void fromData(MarketData data) {
        for (ICommodity g : data.goods)
        {
            //Create commodity index
            _goodTypes.add(g);

            double v = 1.0;
            if (g.getName().compareTo("metal") == 0)
                v = 2.0;
             
            if (g.getName().compareTo("tools") == 0)
                v = 3.0;
             
            history.registerCommodity(g);
            history.prices.add(g,v);
            //start the bidding at $1!
            history.asks.add(g,v);
            //start history charts with 1 fake buy/sell bid
            history.bids.add(g,v);
            history.trades.add(g,v);
        }
        _mapAgents = new HashMap<>();

        for (AgentData aData : data.agentTypes)
        {
            _mapAgents.put(aData.getClassName(), aData);
            history.profit.register(aData.getClassName());
        }
        //Make the agent list
        _agents = new ArrayList<>();
        for (IAgent agent : data.agents)
        {
            _agents.add(agent);
        }
    }

    private static double listAvgf(List<Double> list) {
        double avg = 0;
        for (int j = 0;j < list.size();j++)
        {
            avg += list.get(j);
        }
        avg /= list.size();
        return avg;
    }

    private void resolveOffers() {
        for(Map.Entry<ICommodity, List<Offer>> asks : _book.asks.entrySet()) {
            double count = 0;

            for(Offer o : asks.getValue())
                count += o.units;

            history.asks.add(asks.getKey(), count);

        }

        for(Map.Entry<ICommodity, List<Offer>> bids : _book.bids.entrySet()) {
            double count = 0;

            for(Offer o : bids.getValue())
                count += o.units;

            history.bids.add(bids.getKey(), count);
        }

        Map<ICommodity, OfferResolutionStatistics> r = offerResolver.resolve(offerExecutor, new HashMap<>(_book.bids), new HashMap<>(_book.asks));
        _book.bids.clear();
        _book.asks.clear();

        for(Map.Entry<ICommodity, OfferResolutionStatistics> e : r.entrySet()) {
            OfferResolutionStatistics stats = e.getValue();
            history.trades.add(e.getKey(), stats.unitsTraded);

            if (stats.unitsTraded > 0) {
                history.prices.add(e.getKey(), stats.moneyTraded / stats.unitsTraded);
            } else {
                //special case: none were traded this round, use last round's average price
                history.prices.add(e.getKey(), history.prices.average(e.getKey(), 1));
            }
        }
        List<IAgent> ag = new ArrayList<>(_agents);//.<IAgent>ToList();
        ag.sort(Comparator.comparing(IAgent::getClassName));
        String curr_class = "";
        String last_class = "";
        List<Double> list = null;

        for (int i = 0;i < ag.size();i++)
        {
            IAgent a = ag.get(i);
            //get current agent
            curr_class = a.getClassName();
            //check its class
            if (curr_class.compareTo(last_class) != 0)
            {
                //new class?
                if (list != null)
                {
                    //do we have a list built up?
                    //log last class' profit
                    history.profit.add(last_class, listAvgf(list));
                }

                list = new ArrayList<Double>();
                //make a new list
                last_class = curr_class;
            }

            list.add(a.get_profit());
        }

        //push profit onto list
        //add the last class too
        if(list != null)
            history.profit.add(last_class, listAvgf(list));
    }
}


