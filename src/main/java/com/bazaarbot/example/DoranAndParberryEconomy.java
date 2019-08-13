//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.example;

import com.bazaarbot.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoranAndParberryEconomy  extends Economy
{
    public DoranAndParberryEconomy() throws Exception {
        Market market = new Market("default",this, new DefaultContractResolver());
        MarketData data = getMarketData();
        market.init(data);
        // market.init(MarketData.fromJSON(Json.parse(Assets.getText("assets/settings.json")), getAgent));
        addMarket(market);
    }

    private MarketData getMarketData() throws Exception {
        List<Good> goods = new ArrayList<>();
        List<AgentData> agentTypes = new ArrayList<AgentData>();
        List<BasicAgent> agents = new ArrayList<BasicAgent>();
        goods.add(new Good("food",0.5));
        goods.add(new Good("wood",1.0));
        goods.add(new Good("ore",1.0));
        goods.add(new Good("metal",1.0));
        goods.add(new Good("tools",1.0));
        goods.add(new Good("work",0.1));

        agentTypes.add(new AgentData("farmer",100,"farmer"));
        agentTypes.add(new AgentData("miner",100,"miner"));
        agentTypes.add(new AgentData("refiner",100,"refiner"));
        agentTypes.add(new AgentData("woodcutter",100,"woodcutter"));
        agentTypes.add(new AgentData("blacksmith",100,"blacksmith"));
        agentTypes.add(new AgentData("worker",10,"worker"));

        InventoryData ii;

        //farmer
        HashMap<String, Double> ideal = new HashMap<>();
        HashMap<String, Double> start = new HashMap<>();
        HashMap<String, Double> size = new HashMap<>();
        ideal.put("food", 0.0);
        ideal.put("tools", 1.0);
        ideal.put("wood", 3.0);
        ideal.put("work", 3.0);
        start.put("food", 1.0);
        start.put("tools", 1.0);
        start.put("wood", 0.0);
        start.put("work", 0.0);
        size = null;
        ii = new InventoryData(20, ideal, start,null);
        agentTypes.get(0).inventory = ii;

        //miner
        ideal = new HashMap<>();
        start = new HashMap<>();
        size = new HashMap<>();
        ideal.put("food", 3.0);
        ideal.put("tools", 1.0);
        ideal.put("ore", 0.0);
        start.put("food", 1.0);
        start.put("tools", 1.0);
        start.put("ore", 0.0);

        ii = new InventoryData(20, ideal, start,null);
        agentTypes.get(1).inventory = ii;


        //refiner
        ideal = new HashMap<>();
        start = new HashMap<>();
        size = new HashMap<>();
        ideal.put("food", 3.0);
        ideal.put("tools", 1.0);
        ideal.put("ore", 5.0);
        start.put("food", 1.0);
        start.put("tools", 1.0);
        start.put("ore", 0.0);

        ii = new InventoryData(20, ideal, start,null);
        agentTypes.get(2).inventory = ii;


        //woodcutter
        ideal = new HashMap<>();
        start = new HashMap<>();
        size = new HashMap<>();
        ideal.put("food", 3.0);
        ideal.put("tools", 1.0);
        ideal.put("wood", 0.0);
        start.put("food", 1.0);
        start.put("tools", 1.0);
        start.put("wood", 0.0);

        ii = new InventoryData(20, ideal, start,null);
        agentTypes.get(3).inventory = ii;
        //blacksmith
        ideal = new HashMap<>();
        start = new HashMap<>();
        size = new HashMap<>();
        ideal.put("food", 3.0);
        ideal.put("tools", 1.0);
        ideal.put("metal", 5.0);
        start.put("food", 1.0);
        start.put("tools", 0.0);
        start.put("metal", 0.0);
        start.put("ore", 0.0);

        ii = new InventoryData(20, ideal, start, null);
        agentTypes.get(4).inventory = ii;

        //worker
        ideal = new HashMap<>();
        start = new HashMap<>();
        size = new HashMap<>();
        ideal.put("food", 3.0);
        start.put("food", 1.0);

        ii = new InventoryData(20, ideal, start,null);
        agentTypes.get(5).inventory = ii;

        int idc = 0;
        for (int iagent = 0;iagent < agentTypes.size();iagent++)
        {
            for (int i = 0;i < 5;i++)
            {
                agents.add(getAgent(agentTypes.get(iagent)));
                agents.get(agents.size() - 1).id = idc++;
            }
        }
        MarketData data = new MarketData(goods,agentTypes,agents);
        return data;
    }

    @Override
    public void signalBankrupt(Market m, BasicAgent a) throws Exception {
        replaceAgent(m,a);
    }

    private void replaceAgent(Market market, BasicAgent agent) throws Exception {
        String bestClass = market.getMostProfitableAgentClass();
        //Special case to deal with very high demand-to-supply ratios
        //This will make them favor entering an underserved market over
        //Just picking the most profitable class
        String bestGood = market.getHottestGood();
        if (!bestGood.isEmpty())
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
    public String getAgentClassThatMakesMost(String good) throws Exception {
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
              
        return res;
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
    private BasicAgent getAgent(AgentData data) throws Exception {
        data.logic = getLogic(data.logicName);
        return new Agent(0,data);
    }

    private Logic getLogic(String str) throws Exception {
        String __dummyScrutVar0 = str;
        if (__dummyScrutVar0.equals("blacksmith"))
        {
            return new LogicBlacksmith();
        }
        else if (__dummyScrutVar0.equals("farmer"))
        {
            return new LogicFarmer();
        }
        else if (__dummyScrutVar0.equals("miner"))
        {
            return new LogicMiner();
        }
        else if (__dummyScrutVar0.equals("refiner"))
        {
            return new LogicRefiner();
        }
        else if (__dummyScrutVar0.equals("woodcutter"))
        {
            return new LogicWoodcutter();
        }
        else if (__dummyScrutVar0.equals("worker"))
        {
            return new LogicWorker();
        }
              
        return null;
    }

}


