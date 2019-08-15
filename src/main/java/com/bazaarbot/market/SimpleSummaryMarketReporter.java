package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.history.History;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SimpleSummaryMarketReporter {
    private final MarketSnapshot snapshot;

    public SimpleSummaryMarketReporter(MarketSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    private static String format_row(String cols) {
        StringBuilder row = new StringBuilder();

        for (String c : cols.split("\\s+")) {
            String working = c.trim();
            row.append(padRight(working, 15)).append("|");
        }

        return row.toString();
    }

    public String produce(int rounds) {
        StringBuilder strListGood = new StringBuilder("Commodities\n\n");
        StringBuilder strListGoodPrices = new StringBuilder("Price\n\n");
        StringBuilder strListGoodTrades = new StringBuilder("Trades\n\n");
        StringBuilder strListGoodAsks = new StringBuilder("Supply\n\n");
        StringBuilder strListGoodBids = new StringBuilder("Demand\n\n");
        StringBuilder strListAgent = new StringBuilder("Classes\n\n");
        StringBuilder strListAgentCount = new StringBuilder("Count\n\n");
        StringBuilder strListAgentProfit = new StringBuilder("Profit\n\n");
        StringBuilder strListAgentMoney = new StringBuilder("Money\n\n");
        List<String> setarrStrListInventory = new ArrayList<>();

        History history = snapshot.getHistory();

        ICommodity[] goodTypes = history.getCommodities();

        Arrays.sort(goodTypes, Comparator.comparing(ICommodity::getName));

        for (ICommodity commodity : goodTypes) {
            strListGood.append(commodity).append("\n");
            Double price = history.getPrices().average(commodity, rounds);
            strListGoodPrices.append(String.format("%.2f\n", price));
            Double asks = history.getAsks().average(commodity, rounds);
            strListGoodAsks.append(asks.intValue()).append("\n");
            Double bids = history.getBids().average(commodity, rounds);
            strListGoodBids.append(bids.intValue()).append("\n");
            Double trades = history.getTrades().average(commodity, rounds);
            strListGoodTrades.append(trades.intValue()).append("\n");
            setarrStrListInventory.add(commodity + "\n\n");
        }

        List<String> agentClasses = new ArrayList<>();

        for (AgentSnapshot s : snapshot.getAgents())
            if (!agentClasses.contains(s.getClassName()))
                agentClasses.add(s.getClassName());

        for (String key : agentClasses) {
            List<Double> inventory = new ArrayList<>();
            for (ICommodity str : goodTypes) {
                inventory.add(0.0);
            }
            strListAgent.append(key).append("\n");
            Double profit = history.getProfit().average(key, rounds);
            strListAgentProfit.append(String.format("%.2f\n", profit));

            int count = 0;
            double money = 0;
            for (AgentSnapshot a : snapshot.getAgents()) {
                if (a.getClassName().compareTo(key) == 0) {
                    count++;
                    money += a.getMoney();
                    for (int lic = 0; lic < goodTypes.length; lic++) {
                        inventory.add(lic, inventory.get(lic) + a.getInventory().query(goodTypes[lic]));
                    }
                }

            }
            money /= count;
            for (int lic = 0; lic < goodTypes.length; lic++) {
                inventory.add(lic, inventory.get(lic) / count);
                setarrStrListInventory.add(lic, setarrStrListInventory.get(lic) + String.format("%d\n", lic));
            }
            strListAgentCount.append(String.format("%d\n", count));
            strListAgentMoney.append(String.format("%d\n", (int) money));
        }

        return String.join("\n", new String[]{
                format_row(strListGood.toString()),
                format_row(strListGoodPrices.toString()),
                format_row(strListGoodTrades.toString()),
                format_row(strListGoodBids.toString()),
                format_row(strListGoodAsks.toString()),
        });
    }

}
