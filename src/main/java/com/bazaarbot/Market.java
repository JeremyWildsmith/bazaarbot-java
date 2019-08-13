//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Market   
{
    public String name;
    /**Logs information about all economic activity in this market**/
    public History history;
    /**Signal fired when an agent's money reaches 0 or below**/
    public ISignalBankrupt signalBankrupt;
    /********PRIVATE*********/
    private int _roundNum = 0;
    private List<String> _goodTypes;
    //list of string ids for all the legal commodities
    public List<BasicAgent> _agents = new ArrayList<>();
    public TradeBook _book;
    private HashMap<String,AgentData> _mapAgents;
    private HashMap<String,Good> _mapGoods;
    public Market(String name, ISignalBankrupt isb) throws Exception {
        this.name = name;
        history = new History();
        _book = new TradeBook();
        _goodTypes = new ArrayList<>();
        _agents = new ArrayList<>();
        _mapGoods = new HashMap<String,Good>();
        _mapAgents = new HashMap<String,AgentData>();
        signalBankrupt = isb;
    }

    //new TypedSignal<Market->BasicAgent->Void>();
    public void init(MarketData data) throws Exception {
        fromData(data);
    }

    public int numTypesOfGood() throws Exception {
        return _goodTypes.size();
    }

    public int numAgents() throws Exception {
        return _agents.size();
    }

    public void replaceAgent(BasicAgent oldAgent, BasicAgent newAgent) throws Exception {
        newAgent.id = oldAgent.id;
        _agents.set(oldAgent.id, newAgent);
        oldAgent.destroy();
        newAgent.init(this);
    }

    //@:access(bazaarbot.agent.BasicAgent)    //dfs stub ????
    public void simulate(int rounds) throws Exception {
        for (int round = 0;round < rounds;round++)
        {
            for (BasicAgent agent : _agents)
            {
                agent.moneyLastRound = agent.getMoney();
                agent.simulate(this);
                for (String commodity : _goodTypes)
                {
                    agent.generateOffers(this, commodity);
                }
            }
            for (String commodity : _goodTypes)
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

    public void ask(Offer offer) throws Exception {
        _book.ask(offer);
    }

    public void bid(Offer offer) throws Exception {
        _book.bid(offer);
    }

    /**
    	     * Returns the historical mean price of the given commodity over the last X rounds
    	     * @param	good string id of commodity
    	     * @param	range number of rounds to look back
    	     * @return
    	     */
    public double getAverageHistoricalPrice(String good, int range) throws Exception {
        return history.prices.average(good,range);
    }

    /**
    	     * Get the good with the highest demand/supply ratio over time
    	     * @param   minimum the minimum demand/supply ratio to consider an opportunity
    	     * @param	range number of rounds to look back
    	     * @return
    	     */
    public String getHottestGood(double minimum, int range) throws Exception {
        String best_market = "";
        double best_ratio = -99999;
        for (String good : _goodTypes)
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


    public String getHottestGood() throws Exception {
        return getHottestGood(1.5, 10);
    }

    /**
    	     * Returns the good that has the lowest average price over the given range of time
    	     * @param	range how many rounds to look back
    	     * @param	exclude goods to exclude
    	     * @return
    	     */
    public String getCheapestGood(int range, List<String> exclude) throws Exception {
        double best_price = -9999999;
        // Math.POSITIVE_INFINITY;
        String best_good = "";
        for (String g : _goodTypes)
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
    public String getDearestGood(int range, List<String> exclude) throws Exception {
        double best_price = 0;
        String best_good = "";
        for (String g : _goodTypes)
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
    public String getMostProfitableAgentClass(int range) throws Exception {
        double best = -999999;
        // Math.NEGATIVE_INFINITY;
        String bestClass = "";
        for (String className : _mapAgents.keySet())
        {
            double val = history.profit.average(className,range);
            if (val > best)
            {
                bestClass = className;
                best = val;
            }
             
        }
        return bestClass;
    }

    public final String getMostProfitableAgentClass() throws Exception {
        return getMostProfitableAgentClass(10);
    }

    public AgentData getAgentClass(String className) throws Exception {
        return _mapAgents.get(className);
    }

    public List<String> getAgentClassNames() throws Exception {
        List<String> agentData = new ArrayList<String>();
        for (String key : _mapAgents.keySet())
        {
            agentData.add(key);
        }
        return agentData;
    }

    public List<String> getGoods() throws Exception {
        return new ArrayList<String>(_goodTypes);
    }

    public List<String> getGoods_unsafe() throws Exception {
        return _goodTypes;
    }

    public Good getGoodEntry(String str) throws Exception {
        if (_mapGoods.containsKey(str))
        {
            return _mapGoods.get(str).copy();
        }
         
        return null;
    }

    /********REPORT**********/
    public MarketReport get_marketReport(int rounds) throws Exception {
        MarketReport mr = new MarketReport();
        mr.strListGood = "Commodities\n\n";
        mr.strListGoodPrices = "Price\n\n";
        mr.strListGoodTrades = "Trades\n\n";
        mr.strListGoodAsks = "Supply\n\n";
        mr.strListGoodBids = "Demand\n\n";
        mr.strListAgent = "Classes\n\n";
        mr.strListAgentCount = "Count\n\n";
        mr.strListAgentProfit = "Profit\n\n";
        mr.strListAgentMoney = "Money\n\n";
        mr.setarrStrListInventory(new ArrayList<String>());
        for (String commodity : _goodTypes)
        {
            mr.strListGood += commodity + "\n";
            Double price = history.prices.average(commodity,rounds);
            mr.strListGoodPrices += Quick.numStr(price,2) + "\n";
            Double asks = history.asks.average(commodity,rounds);
            mr.strListGoodAsks += ((asks.intValue())+"\n");
            Double bids = history.bids.average(commodity,rounds);
            mr.strListGoodBids += (bids.intValue())+"\n";
            Double trades = history.trades.average(commodity,rounds);
            mr.strListGoodTrades += (trades.intValue())+"\n";
            mr.getarrStrListInventory().add(commodity + "\n\n");
        }
        for (String key : _mapAgents.keySet())
        {
            List<Double> inventory = new ArrayList<Double>();
            for (String str : _goodTypes)
            {
                inventory.add(0.0);
            }
            mr.strListAgent += key + "\n";
            Double profit = history.profit.average(key,rounds);
            mr.strListAgentProfit += Quick.numStr(profit,2) + "\n";
            double test_profit = 0;
            List<BasicAgent> list = _agents;

            int count = 0;
            double money = 0;
            for (BasicAgent a : list)
            {
                if (a.getClassName().compareTo(key) == 0)
                {
                    count++;
                    money += a.getMoney();
                    for (int lic = 0;lic < _goodTypes.size();lic++)
                    {
                        inventory.add(lic, inventory.get(lic) + a.queryInventory(_goodTypes.get(lic)));
                    }
                }
                 
            }
            money /= count;
            for (int lic = 0;lic < _goodTypes.size();lic++)
            {
                inventory.add(lic, inventory.get(lic) / count);
                mr.getarrStrListInventory().add(lic, mr.getarrStrListInventory().get(lic) + (Quick.numStr(inventory.get(lic),1) + "\n"));
            }
            mr.strListAgentCount += Quick.numStr(count,0) + "\n";
            mr.strListAgentMoney += Quick.numStr(money,0) + "\n";
        }
        return mr;
    }

    /********PRIVATE*********/
    private void fromData(MarketData data) throws Exception {
        for (Good g : data.goods)
        {
            //Create commodity index
            _goodTypes.add(g.id);
            _mapGoods.put(g.id, new Good(g.id,g.size));
            double v = 1.0;
            if (g.id.compareTo("metal") == 0)
                v = 2.0;
             
            if (g.id.compareTo("tools") == 0)
                v = 3.0;
             
            history.register(g.id);
            history.prices.add(g.id,v);
            //start the bidding at $1!
            history.asks.add(g.id,v);
            //start history charts with 1 fake buy/sell bid
            history.bids.add(g.id,v);
            history.trades.add(g.id,v);
            _book.register(g.id);
        }
        _mapAgents = new HashMap<String,AgentData>();
        for (AgentData aData : data.agentTypes)
        {
            _mapAgents.put(aData.className, aData);
            history.profit.register(aData.className);
        }
        //Make the agent list
        _agents = new ArrayList<>();
        int agentIndex = 0;
        for (BasicAgent agent : data.agents)
        {
            agent.id = agentIndex;
            agent.init(this);
            _agents.add(agent);
            agentIndex++;
        }
    }

    private void resolveOffers(String good) throws Exception {
        List<Offer> bids = _book.bids.get(good);
        List<Offer> asks = _book.asks.get(good);
        bids = Quick.shuffle(bids);
        asks = Quick.shuffle(asks);
        //bids.Sort(Quick.sortOfferDecending); //highest buying price first
        asks.sort(Quick.sortOfferAcending);
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
            Double quantity_traded = (double)Math.min(seller.units,buyer.units);
            Double clearing_price = seller.unit_price;
            //Quick.avgf(seller.unit_price, buyer.unit_price);
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
            avgPrice = moneyTraded / (double)unitsTraded;
            history.prices.add(good,avgPrice);
        }
        else
        {
            //special case: none were traded this round, use last round's average price
            history.prices.add(good,history.prices.average(good,1));
            avgPrice = history.prices.average(good,1);
        } 
        List<BasicAgent> ag = new ArrayList<>(_agents);//.<BasicAgent>ToList();
        ag.sort(Quick.sortAgentAlpha);
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
                    history.profit.add(last_class,Quick.listAvgf(list));
                }
                 
                list = new ArrayList<Double>();
                //make a new list
                last_class = curr_class;
            }
             
            list.add(a.get_profit());
        }
        //push profit onto list
        //add the last class too
        history.profit.add(last_class,Quick.listAvgf(list));
    }

    //sort by id so everything works again
    //_agents.Sort(Quick.sortAgentId);
    private void transferGood(String good, double units, int seller_id, int buyer_id, double clearing_price) throws Exception {
        BasicAgent seller = _agents.get(seller_id);
        BasicAgent buyer = _agents.get(buyer_id);
        seller.changeInventory(good, -units, 0);
        buyer.changeInventory(good, units, clearing_price);
    }

    private void transferMoney(double amount, int seller_id, int buyer_id) throws Exception {
        BasicAgent seller = _agents.get(seller_id);
        BasicAgent  buyer = _agents.get(buyer_id);
        seller.setMoney(seller.getMoney() + amount);
        buyer.setMoney(buyer.getMoney() - amount);
    }

}


