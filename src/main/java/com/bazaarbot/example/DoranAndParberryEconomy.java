//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.*;
import com.bazaarbot.agent.Agent;
import com.bazaarbot.agent.AgentData;
import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.contract.DefaultContractResolver;
import com.bazaarbot.inventory.InventoryData;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.MarketData;

import java.util.*;

public class DoranAndParberryEconomy  extends Economy
{
    private final Random rng;

    public DoranAndParberryEconomy() {
        this(new Random());
    }

    public DoranAndParberryEconomy(Random rng) {
        this.rng = rng;
        Market market = new Market("default", getMarketData(), this, new DefaultContractResolver(), rng);
        addMarket(market);
    }

    private MarketData getMarketData() {
        List<AgentData> agentTypes = new ArrayList<AgentData>();
        List<BasicAgent> agents = new ArrayList<BasicAgent>();

        agentTypes.add(new AgentData("farmer",100,"farmer"));
        agentTypes.add(new AgentData("miner",100,"miner"));
        agentTypes.add(new AgentData("refiner",100,"refiner"));
        agentTypes.add(new AgentData("woodcutter",100,"woodcutter"));
        agentTypes.add(new AgentData("blacksmith",100,"blacksmith"));
        agentTypes.add(new AgentData("worker",10,"worker"));

        InventoryData ii;

        //farmer
        HashMap<ICommodity, Double> ideal = new HashMap<>();
        HashMap<ICommodity, Double> start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 0.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Wood, 3.0);
        ideal.put(ExampleCommodity.Work, 3.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Wood, 0.0);
        start.put(ExampleCommodity.Work, 0.0);
        ii = new InventoryData(20, ideal, start);
        agentTypes.get(0).setInventory(ii);

        //miner
        ideal = new HashMap<>();
        start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Ore, 0.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Ore, 0.0);

        ii = new InventoryData(20, ideal, start);
        agentTypes.get(1).setInventory(ii);


        //refiner
        ideal = new HashMap<>();
        start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Ore, 5.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Ore, 0.0);

        ii = new InventoryData(20, ideal, start);
        agentTypes.get(2).setInventory(ii);


        //woodcutter
        ideal = new HashMap<>();
        start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Wood, 0.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Wood, 0.0);

        ii = new InventoryData(20, ideal, start);
        agentTypes.get(3).setInventory(ii);
        //blacksmith
        ideal = new HashMap<>();
        start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Metal, 5.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 0.0);
        start.put(ExampleCommodity.Metal, 0.0);
        start.put(ExampleCommodity.Ore, 0.0);

        ii = new InventoryData(20, ideal, start);
        agentTypes.get(4).setInventory(ii);

        //worker
        ideal = new HashMap<>();
        start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        start.put(ExampleCommodity.Food, 1.0);

        ii = new InventoryData(20, ideal, start);
        agentTypes.get(5).setInventory(ii);

        int idc = 0;
        for (int iagent = 0;iagent < agentTypes.size();iagent++)
        {
            for (int i = 0;i < 5;i++)
            {
                agents.add(getAgent(agentTypes.get(iagent)));
                agents.get(agents.size() - 1).id = idc++;
            }
        }
        MarketData data = new MarketData(Arrays.asList(ExampleCommodity.values()), agentTypes,agents);
        return data;
    }

    @Override
    public void signalBankrupt(Market m, BasicAgent a) {
        replaceAgent(m,a);
    }

    private void replaceAgent(Market market, BasicAgent agent) {
        String bestClass = market.getMostProfitableAgentClass();
        //Special case to deal with very high demand-to-supply ratios
        //This will make them favor entering an underserved market over
        //Just picking the most profitable class
        ICommodity bestGood = market.getHottestGood();
        if (bestGood != null)
        {
            String bestGoodClass = getAgentClassThatMakesMost(bestGood);
            if (!bestGoodClass.isEmpty())
            {
                bestClass = bestGoodClass;
            }
             
        }
         
        BasicAgent newAgent = getAgent(market.getAgentClass(bestClass));
        market.replaceAgent(agent, newAgent);
    }

    /**
    	     * Find the agent class that produces the most of a given good
    	     * @param	good
    	     * @return
    	     */
    public String getAgentClassThatMakesMost(ICommodity good) {
        throw new RuntimeException("Not Implemented.");/*
        String res = "";
        if (good.compareTo("food") == 0)
        {
            res = "farmer";
        }
        else if (good.compareTo("wood") == 0)
        {
            res = "woodcutter";
        }
        else if (good.compareTo("ore") == 0)
        {
            res = "miner";
        }
        else if (good.compareTo("metal") == 0)
        {
            res = "refiner";
        }
        else if (good.compareTo("tools") == 0)
        {
            res = "blacksmith";
        }
        else if (good.compareTo("work") == 0)
        {
            res = "worker";
        }
              
        return res;*/
    }

    /**
    	     * Find the agent class that has the most of a given good
    	     * @return
    	     */
    /*
    	    public function getAgentClassWithMost(good:String):String
    	    {
    		    var amount:Float = 0;
    		    var bestAmount:Float = 0;
    		    var bestClass:String = "";
    		    for (key in _mapAgents.keys())
    		    {
    			    amount = getAverageInventory(key, good);
    			    if (amount > bestAmount)
    			    {
    				    bestAmount = amount;
    				    bestClass = key;
    			    }
    		    }
    		    return bestClass;
    	    }
    	    */
    //private BasicAgent getAgentScript(AgentData data)
    //{
    //    data.logic = new LogicScript(data.logicName+".hs");
    //    return new Agent(0, data);
    //}
    private BasicAgent getAgent(AgentData data) {
        data.setLogic(getLogic(data.getLogicName()));
        return new Agent(0,data);
    }

    private Logic getLogic(String str) {
        String __dummyScrutVar0 = str;
        if (__dummyScrutVar0.equals("blacksmith"))
        {
            return new LogicBlacksmith(rng);
        }
        else if (__dummyScrutVar0.equals("farmer"))
        {
            return new LogicFarmer(rng);
        }
        else if (__dummyScrutVar0.equals("miner"))
        {
            return new LogicMiner(rng);
        }
        else if (__dummyScrutVar0.equals("refiner"))
        {
            return new LogicRefiner(rng);
        }
        else if (__dummyScrutVar0.equals("woodcutter"))
        {
            return new LogicWoodcutter(rng);
        }
        else if (__dummyScrutVar0.equals("worker"))
        {
            return new LogicWorker(rng);
        }
              
        return null;
    }

}


