//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.*;
import com.bazaarbot.agent.AgentData;
import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.History;
import com.bazaarbot.inventory.Inventory;

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
    public List<BasicAgent> _agents = new ArrayList<>();
    public TradeBook _book;
    private HashMap<String, AgentData> _mapAgents;

    private final IContractResolver _contractResolver;

    private final Random rng;

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IContractResolver contractResolver) {
        this(name, marketData, isb, contractResolver, new Random());
    }

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IContractResolver contractResolver, Random rng) {
        this.name = name;
        history = new History();
        _book = new TradeBook();
        _goodTypes = new ArrayList<>();
        _agents = new ArrayList<>();
        _mapAgents = new HashMap<>();
        signalBankrupt = isb;
        _contractResolver = contractResolver;
        this.rng = rng;
        fromData(marketData);
    }

    public void replaceAgent(BasicAgent oldAgent, BasicAgent newAgent) {
        newAgent.id = oldAgent.id;
        _agents.set(oldAgent.id, newAgent);
    }

    //@:access(bazaarbot.agent.BasicAgent)    //dfs stub ????
    public void simulate(int rounds) {
        for (int round = 0;round < rounds;round++)
        {
            for (BasicAgent agent : _agents)
            {
                agent.moneyLastRound = agent.getMoney();
                agent.simulate(this);
                for (ICommodity commodity : _goodTypes)
                {
                    agent.generateOffers(this, commodity);
                }
            }
            for (ICommodity commodity : _goodTypes)
            {
                resolveOffers(commodity);
            }
            List<BasicAgent> del = new ArrayList<BasicAgent>();
            for (BasicAgent agent : _agents)
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
        List<String> agentData = new ArrayList<String>();
        for (String key : _mapAgents.keySet())
        {
            agentData.add(key);
        }
        return agentData;
    }

    public MarketSnapshot getSnapshot() {
        List<AgentSnapshot> agentData = new ArrayList<>();
        for(BasicAgent a : _agents) {
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
            _book.register(g);
        }
        _mapAgents = new HashMap<>();

        for (AgentData aData : data.agentTypes)
        {
            _mapAgents.put(aData.getClassName(), aData);
            history.profit.register(aData.getClassName());
        }
        //Make the agent list
        _agents = new ArrayList<>();
        int agentIndex = 0;
        for (BasicAgent agent : data.agents)
        {
            agent.id = agentIndex;
            _agents.add(agent);
            agentIndex++;
        }
    }

    private static void sortOffers(List<Offer> offers) {
        offers.sort((Offer a, Offer b) -> {
            if (a.unit_price < b.unit_price)
                return -1;

            if (a.unit_price > b.unit_price)
                return 1;

            return 0;
        });
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

    private void resolveOffers(ICommodity good) {
        List<Offer> bids = _book.bids.get(good);
        List<Offer> asks = _book.asks.get(good);
        Collections.shuffle(bids, rng);
        Collections.shuffle(asks, rng);
        //bids.Sort(Utils.sortOfferDecending); //highest buying price first
        sortOffers(asks);
        //lowest selling price first
        int successfulTrades = 0;
        //# of successful trades this round
        double moneyTraded = 0;
        //amount of money traded this round
        double unitsTraded = 0;
        //amount of goods traded this round
        double avgPrice = 0;
        //avg clearing price this round
        double numAsks = 0;
        double numBids = 0;
        int failsafe = 0;
        for (int i = 0;i < bids.size();i++)
        {
            numBids += bids.get(i).units;
        }
        for (int i = 0;i < asks.size();i++)
        {
            numAsks += asks.get(i).units;
        }
        while (bids.size() > 0 && asks.size() > 0)
        {
            //march through and try to clear orders
            //while both books are non-empty
            Offer buyer = bids.get(0);
            Offer seller = asks.get(0);
            Double quantity_traded = Math.min(seller.units,buyer.units);
            Double clearing_price = seller.unit_price;
            //Utils.avgf(seller.unit_price, buyer.unit_price);
            //if (buyer.unit_price < seller.unit_price)
            //    break;
            if (quantity_traded > 0)
            {
                //transfer the goods for the agreed price
                seller.units -= quantity_traded;
                buyer.units -= quantity_traded;
                transferGood(good,quantity_traded,seller.agent_id,buyer.agent_id,clearing_price);
                transferMoney(quantity_traded * clearing_price,seller.agent_id,buyer.agent_id);
                //update agent price beliefs based on successful transaction
                BasicAgent buyer_a = _agents.get(buyer.agent_id);
                BasicAgent seller_a = _agents.get(seller.agent_id);
                buyer_a.updatePriceModel(this, "buy", good, true, clearing_price);
                seller_a.updatePriceModel(this, "sell", good, true, clearing_price);
                //log the stats
                moneyTraded += (quantity_traded * clearing_price);
                unitsTraded += quantity_traded;
                successfulTrades++;
            }
             
            if (seller.units == 0)
            {
                //seller is out of offered good
                asks.remove(0);
                //.splice(0, 1);		//remove ask
                failsafe = 0;
            }
             
            if (buyer.units == 0)
            {
                //buyer is out of offered good
                bids.remove(0);
                //.splice(0, 1);		//remove bid
                failsafe = 0;
            }
             
            failsafe++;
            if (failsafe > 1000)
            {
                System.out.println("BOINK!");
            }
             
        }
        while (bids.size() > 0)
        {
            //reject all remaining offers,
            //update price belief models based on unsuccessful transaction
            Offer buyer = bids.get(0);
            BasicAgent buyer_a = _agents.get(buyer.agent_id);
            buyer_a.updatePriceModel(this, "buy", good, false);
            bids.remove(0);
        }
        while (asks.size() > 0)
        {
            //.splice(0, 1);
            Offer seller = asks.get(0);
            BasicAgent seller_a = _agents.get(seller.agent_id);
            seller_a.updatePriceModel(this, "sell", good, false);
            asks.remove(0);
        }
        // splice(0, 1);
        //update history
        history.asks.add(good,numAsks);
        history.bids.add(good,numBids);
        history.trades.add(good,unitsTraded);
        if (unitsTraded > 0)
        {
            avgPrice = moneyTraded / unitsTraded;
            history.prices.add(good,avgPrice);
        }
        else
        {
            //special case: none were traded this round, use last round's average price
            history.prices.add(good,history.prices.average(good,1));
            avgPrice = history.prices.average(good,1);
        } 
        List<BasicAgent> ag = new ArrayList<>(_agents);//.<BasicAgent>ToList();
        ag.sort(Comparator.comparing(BasicAgent::getClassName));
        String curr_class = "";
        String last_class = "";
        List<Double> list = null;
        double avg_profit = 0;
        for (int i = 0;i < ag.size();i++)
        {
            BasicAgent a = ag.get(i);
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
        history.profit.add(last_class, listAvgf(list));
    }

    //sort by id so everything works again
    //_agents.Sort(Utils.sortAgentId);
    private void transferGood(ICommodity good, double units, int seller_id, int buyer_id, double clearing_price) {

        BasicAgent seller = _agents.get(seller_id);
        BasicAgent  buyer = _agents.get(buyer_id);
        _contractResolver.newContract(seller, buyer, good, units, clearing_price);
    }

    private void transferMoney(double amount, int seller_id, int buyer_id) {
        BasicAgent seller = _agents.get(seller_id);
        BasicAgent  buyer = _agents.get(buyer_id);
        seller.setMoney(seller.getMoney() + amount);
        buyer.setMoney(buyer.getMoney() - amount);
    }
}


