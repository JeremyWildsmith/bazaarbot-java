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
        this.agents.set(oldAgent.getId(), newAgent);
    }

    //@:access(bazaarbot.agent.BasicAgent)    //dfs stub ????
    public void simulate(int rounds) {
        for (int round = 0; round < rounds; round++) {
            for (BasicAgent agent : agents) {
                agent.simulate(this);
                for (ICommodity commodity : goodTypes) {
                    //if (agent.queryInventory(commodity) > 0) {
                        agent.generateOffers(this, commodity);
                    //}
                }
            }
            for (ICommodity commodity : goodTypes) {
                resolveOffers(commodity);
            }
            for (BasicAgent agent : agents) {
                if (agent.getMoneyAvailable() <= 0)
                    signalBankrupt.signalBankrupt(this, agent);

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
            mapAgents.put(aData.getAgentClassName(), aData);
            history.getProfit().register(aData.getAgentClassName());
        }
        //Make the agent list
        agents = new ArrayList<>(data.getAgents());
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
            Offer buyerBid = bids.get(0);
            Offer sellerAsk = asks.get(0);
            double quantityTraded = Math.min(sellerAsk.getUnits(), buyerBid.getUnits());
            double clearingPrice = sellerAsk.getUnitPrice();
            Optional<BasicAgent> seller = agents.stream().filter(basicAgent -> basicAgent.getId() == buyerBid.getAgentId()).findFirst();
            Optional<BasicAgent> buyer = agents.stream().filter(basicAgent -> basicAgent.getId() == sellerAsk.getAgentId()).findFirst();
            // If we do not have such ids
            if (!seller.isPresent() || !buyer.isPresent()) {
                return;
            }
            BasicAgent buyerA = buyer.get();
            BasicAgent sellerA = seller.get();

            //Utils.avgf(seller.unit_price, buyer.unit_price);
            //if (buyer.unit_price < seller.unit_price)
            //    break;
            if (quantityTraded > 0) {
                //transfer the goods for the agreed price
                sellerAsk.setUnits(sellerAsk.getUnits() - quantityTraded);
                buyerBid.setUnits(buyerBid.getUnits() - quantityTraded);
                transferGood(good, quantityTraded, sellerA, buyerA, clearingPrice);
                transferMoney(quantityTraded * clearingPrice, sellerA, buyerA);
                //update agent price beliefs based on successful transaction
                buyerA.updatePriceModel(this, "buy", good, true, clearingPrice);
                sellerA.updatePriceModel(this, "sell", good, true, clearingPrice);
                //log the stats
                moneyTraded += (quantityTraded * clearingPrice);
                unitsTraded += quantityTraded;
                successfulTrades++;
            }

            if (sellerAsk.getUnits() == 0) {
                //seller is out of offered good
                asks.remove(0);
                //.splice(0, 1);		//remove ask
                failSafe = 0;
            }

            if (buyerBid.getUnits() == 0) {
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
            Optional<BasicAgent> buyerOptional = agents.stream().filter(basicAgent -> basicAgent.getId() == buyer.getAgentId()).findFirst();
            if (!buyerOptional.isPresent()) {
                // no such ids
                return;
            }
            buyerOptional.get().updatePriceModel(this, "buy", good, false);
            bids.remove(0);
        }
        while (asks.size() > 0) {
            //.splice(0, 1);
            Offer seller = asks.get(0);
            Optional<BasicAgent> sellerOptional = agents.stream().filter(basicAgent -> basicAgent.getId() == seller.getAgentId()).findFirst();
            if (!sellerOptional.isPresent()) {
                // no such ids
                return;
            }
            sellerOptional.get().updatePriceModel(this, "sell", good, false);
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
        ag.sort(Comparator.comparing(BasicAgent::getAgentName));
        String currentClass = "";
        String lastClass = "";
        List<Double> list = null;
        double averageProfit = 0;
        for (BasicAgent agent : ag) {
            //get current agent
            currentClass = agent.getAgentName();
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

            list.add(agent.getProfitFromLastRound());
        }
        //push profit onto list
        //add the last class too
        history.getProfit().add(lastClass, listAvgf(list));
    }

    //sort by id so everything works again
    //agents.Sort(Utils.sortAgentId);
    private void transferGood(ICommodity good, double units, BasicAgent seller, BasicAgent buyer, double clearingPrice) {
        contractResolver.newContract(seller, buyer, good, units, clearingPrice);
    }

    private void transferMoney(double amount, BasicAgent seller, BasicAgent buyer) {
        seller.setMoneyAvailable(seller.getMoneyAvailable() + amount);
        buyer.setMoneyAvailable(buyer.getMoneyAvailable() - amount);
    }

    public String getName() {
        return name;
    }
}


