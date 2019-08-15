//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.TradeBook;
import com.bazaarbot.agent.AgentData;
import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.contract.IContractResolver;
import com.bazaarbot.history.History;

import java.util.*;

public class Market {
    private String name;
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
    private List<BasicAgent> agents = new ArrayList<>();
    private TradeBook tradeBook;
    private HashMap<String, AgentData> mapAgents;

    private final IContractResolver contractResolver;

    private final Random rng;

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IContractResolver contractResolver) {
        this(name, marketData, isb, contractResolver, new Random());
    }

    public Market(String name, MarketData marketData, ISignalBankrupt isb, IContractResolver contractResolver, Random rng) {
        this.name = name;
        this.history = new History();
        this.tradeBook = new TradeBook();
        this.goodTypes = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.mapAgents = new HashMap<>();
        this.signalBankrupt = isb;
        this.contractResolver = contractResolver;
        this.rng = rng;
        fromData(marketData);
    }

    public void replaceAgent(BasicAgent oldAgent, BasicAgent newAgent) {
        newAgent.setId(oldAgent.getId());
        this.agents.set(oldAgent.getId(), newAgent);
    }

    //@:access(bazaarbot.agent.BasicAgent)    //dfs stub ????
    public void simulate(int rounds) {
        for (int round = 0; round < rounds; round++) {
            for (BasicAgent agent : agents) {
                agent.setMoneyLastRound(agent.getMoney());
                agent.simulate(this);
                for (ICommodity commodity : goodTypes) {
                    agent.generateOffers(this, commodity);
                }
            }
            for (ICommodity commodity : goodTypes) {
                resolveOffers(commodity);
            }
            List<BasicAgent> del = new ArrayList<>();
            for (BasicAgent agent : agents) {
                if (agent.getMoney() <= 0)
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
            double asks = history.getAsks().average(good, range);
            double bids = history.getBids().average(good, range);
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

    /**
     * @param range
     * @return
     */
    public String getMostProfitableAgentClass(int range) {
        double best = -999999;
        // Math.NEGATIVE_INFINITY;
        String bestClass = "";
        for (String className : mapAgents.keySet()) {
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

    public AgentData getAgentClass(String className) {
        return mapAgents.get(className);
    }

    public List<String> getAgentClassNames() {
        return new ArrayList<>(mapAgents.keySet());
    }

    public MarketSnapshot getSnapshot() {
        List<AgentSnapshot> agentData = new ArrayList<>();
        for (BasicAgent a : agents) {
            agentData.add(a.getSnapshot());
        }

        return new MarketSnapshot(new History(history), agentData);
    }

    /********PRIVATE*********/
    private void fromData(MarketData data) {
        for (ICommodity g : data.getGoods()) {
            //Create commodity index
            goodTypes.add(g);

            double v = 1.0;
            if (g.getName().compareTo("metal") == 0) {
                v = 2.0;
            }

            if (g.getName().compareTo("tools") == 0) {
                v = 3.0;
            }

            history.registerCommodity(g);
            history.getPrices().add(g, v);
            //start the bidding at $1!
            history.getAsks().add(g, v);
            //start history charts with 1 fake buy/sell bid
            history.getBids().add(g, v);
            history.getTrades().add(g, v);
            tradeBook.register(g);
        }
        mapAgents = new HashMap<>();

        for (AgentData aData : data.getAgentTypes()) {
            mapAgents.put(aData.getClassName(), aData);
            history.getProfit().register(aData.getClassName());
        }
        //Make the agent list
        agents = new ArrayList<>();
        int agentIndex = 0;
        for (BasicAgent agent : data.getAgents()) {
            agent.setId(agentIndex);
            agents.add(agent);
            agentIndex++;
        }
    }

    private static void sortOffers(List<Offer> offers) {
        offers.sort(Comparator.comparingDouble(Offer::getUnitPrice));
    }

    private static double listAvgf(List<Double> list) {
        double average = 0;
        for (double aDouble : list) {
            average += aDouble;
        }
        average /= list.size();
        return average;
    }

    private void resolveOffers(ICommodity good) {
        List<Offer> bids = tradeBook.bids.get(good);
        List<Offer> asks = tradeBook.asks.get(good);
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
        double averagePrice = 0;
        //avg clearing price this round
        double numAsks = 0;
        double numBids = 0;
        int failSafe = 0;
        for (Offer bid : bids) {
            numBids += bid.getUnits();
        }
        for (Offer ask : asks) {
            numAsks += ask.getUnits();
        }
        while (bids.size() > 0 && asks.size() > 0) {
            //march through and try to clear orders
            //while both books are non-empty
            Offer buyer = bids.get(0);
            Offer seller = asks.get(0);
            double quantityTraded = Math.min(seller.getUnits(), buyer.getUnits());
            double clearingPrice = seller.getUnitPrice();
            //Utils.avgf(seller.unit_price, buyer.unit_price);
            //if (buyer.unit_price < seller.unit_price)
            //    break;
            if (quantityTraded > 0) {
                //transfer the goods for the agreed price
                seller.setUnits(seller.getUnits() - quantityTraded);
                buyer.setUnits(buyer.getUnits() - quantityTraded);
                transferGood(good, quantityTraded, seller.getAgentId(), buyer.getAgentId(), clearingPrice);
                transferMoney(quantityTraded * clearingPrice, seller.getAgentId(), buyer.getAgentId());
                //update agent price beliefs based on successful transaction
                BasicAgent buyerA = agents.get(buyer.getAgentId());
                BasicAgent sellerA = agents.get(seller.getAgentId());
                buyerA.updatePriceModel(this, "buy", good, true, clearingPrice);
                sellerA.updatePriceModel(this, "sell", good, true, clearingPrice);
                //log the stats
                moneyTraded += (quantityTraded * clearingPrice);
                unitsTraded += quantityTraded;
                successfulTrades++;
            }

            if (seller.getUnits() == 0) {
                //seller is out of offered good
                asks.remove(0);
                //.splice(0, 1);		//remove ask
                failSafe = 0;
            }

            if (buyer.getUnits() == 0) {
                //buyer is out of offered good
                bids.remove(0);
                //.splice(0, 1);		//remove bid
                failSafe = 0;
            }

            failSafe++;
            if (failSafe > 1000) {
                System.out.println("BOINK!");
            }

        }
        while (bids.size() > 0) {
            //reject all remaining offers,
            //update price belief models based on unsuccessful transaction
            Offer buyer = bids.get(0);
            BasicAgent buyerA = agents.get(buyer.getAgentId());
            buyerA.updatePriceModel(this, "buy", good, false);
            bids.remove(0);
        }
        while (asks.size() > 0) {
            //.splice(0, 1);
            Offer seller = asks.get(0);
            BasicAgent sellerA = agents.get(seller.getAgentId());
            sellerA.updatePriceModel(this, "sell", good, false);
            asks.remove(0);
        }
        // splice(0, 1);
        //update history
        history.getAsks().add(good, numAsks);
        history.getBids().add(good, numBids);
        history.getTrades().add(good, unitsTraded);
        if (unitsTraded > 0) {
            averagePrice = moneyTraded / unitsTraded;
            history.getPrices().add(good, averagePrice);
        } else {
            //special case: none were traded this round, use last round's average price
            history.getPrices().add(good, history.getPrices().average(good, 1));
            averagePrice = history.getPrices().average(good, 1);
        }
        List<BasicAgent> ag = new ArrayList<>(agents);//.<BasicAgent>ToList();
        ag.sort(Comparator.comparing(BasicAgent::getClassName));
        String currentClass = "";
        String lastClass = "";
        List<Double> list = null;
        double averageProfit = 0;
        for (BasicAgent agent : ag) {
            //get current agent
            currentClass = agent.getClassName();
            //check its class
            if (currentClass.compareTo(lastClass) != 0) {
                //new class?
                if (list != null) {
                    //do we have a list built up?
                    //log last class' profit
                    history.getProfit().add(lastClass, listAvgf(list));
                }

                list = new ArrayList<>();
                //make a new list
                lastClass = currentClass;
            }

            list.add(agent.getProfit());
        }
        //push profit onto list
        //add the last class too
        history.getProfit().add(lastClass, listAvgf(list));
    }

    //sort by id so everything works again
    //agents.Sort(Utils.sortAgentId);
    private void transferGood(ICommodity good, double units, int sellerId, int buyerId, double clearingPrice) {

        BasicAgent seller = agents.get(sellerId);
        BasicAgent buyer = agents.get(buyerId);
        contractResolver.newContract(seller, buyer, good, units, clearingPrice);
    }

    private void transferMoney(double amount, int sellerId, int buyerId) {
        BasicAgent seller = agents.get(sellerId);
        BasicAgent buyer = agents.get(buyerId);
        seller.setMoney(seller.getMoney() + amount);
        buyer.setMoney(buyer.getMoney() - amount);
    }

    public String getName() {
        return name;
    }
}


