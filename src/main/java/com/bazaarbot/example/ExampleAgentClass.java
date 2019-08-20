package com.bazaarbot.example;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.DefaultAgent;
import com.bazaarbot.agent.IAgentClass;
import com.bazaarbot.agent.IAgentFactory;
import com.bazaarbot.inventory.InventoryData;

import java.util.HashMap;

public enum ExampleAgentClass implements IAgentClass {
    Farmer("farmer", () -> {
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
        InventoryData inv = new InventoryData(20, ideal, start);

        return new DefaultAgent("farmer", new LogicFarmer(), inv, 100);
    }),

    Miner("miner", () -> {
        HashMap<ICommodity, Double> ideal = new HashMap<>();
        HashMap<ICommodity, Double> start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Ore, 0.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Ore, 0.0);

        InventoryData inv = new InventoryData(20, ideal, start);

        return new DefaultAgent("miner", new LogicMiner(), inv, 100);
    }),

    Refiner("refiner", () -> {
        HashMap<ICommodity, Double> ideal = new HashMap<>();
        HashMap<ICommodity, Double> start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Ore, 5.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Ore, 0.0);

        InventoryData inv = new InventoryData(20, ideal, start);

        return new DefaultAgent("refiner", new LogicRefiner(), inv, 100);
    }),

    Woodcutter("woodcutter", () -> {
        HashMap<ICommodity, Double> ideal = new HashMap<>();
        HashMap<ICommodity, Double> start = new HashMap<>();

        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Wood, 0.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 1.0);
        start.put(ExampleCommodity.Wood, 0.0);

        InventoryData inv = new InventoryData(20, ideal, start);

        return new DefaultAgent("woodcutter", new LogicWoodcutter(), inv, 100);
    }),

    blacksmith("blacksmith", () -> {
        HashMap<ICommodity, Double> ideal = new HashMap<>();
        HashMap<ICommodity, Double> start = new HashMap<>();

        ideal.put(ExampleCommodity.Food, 3.0);
        ideal.put(ExampleCommodity.Tools, 1.0);
        ideal.put(ExampleCommodity.Metal, 5.0);
        start.put(ExampleCommodity.Food, 1.0);
        start.put(ExampleCommodity.Tools, 0.0);
        start.put(ExampleCommodity.Metal, 0.0);
        start.put(ExampleCommodity.Ore, 0.0);

        InventoryData inv = new InventoryData(20, ideal, start);

        return new DefaultAgent("blacksmith", new LogicBlacksmith(), inv, 100);
    }),

    Worker("worker", () -> {
        HashMap<ICommodity, Double> ideal = new HashMap<>();
        HashMap<ICommodity, Double> start = new HashMap<>();
        ideal.put(ExampleCommodity.Food, 3.0);
        start.put(ExampleCommodity.Food, 1.0);

        InventoryData inv = new InventoryData(20, ideal, start);

        return new DefaultAgent("worker", new LogicWorker(), inv, 100);
    })
    ;
    private final String name;
    private final IAgentFactory factory;

    ExampleAgentClass(String name, IAgentFactory factory) {
        this.name = name;
        this.factory = factory;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public IAgentFactory getFactory() {
        return this.factory;
    }

    public static ExampleAgentClass getByClassName(String name) {
        for(ExampleAgentClass c : values()) {
            if(c.getName().compareTo(name) == 0)
                return c;
        }

        return null;
    }
}
