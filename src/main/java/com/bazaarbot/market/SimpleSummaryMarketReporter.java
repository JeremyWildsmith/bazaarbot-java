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
        String row = "";

        for(String c : cols.split("\\s+")) {
            String working = c.trim();
            row += padRight(working, 15) + "|";
        }

        return row;
    }

    public String produce(int rounds) {
        String strListGood = "Commodities\n\n";
        String strListGoodPrices = "Price\n\n";
        String strListGoodTrades = "Trades\n\n";
        String strListGoodAsks = "Supply\n\n";
        String strListGoodBids = "Demand\n\n";
        String strListAgent = "Classes\n\n";
        String strListAgentCount = "Count\n\n";
        String strListAgentProfit = "Profit\n\n";
        String strListAgentMoney = "Money\n\n";
        List<String> setarrStrListInventory = new ArrayList<String>();

        History history = snapshot.getHistory();

        ICommodity[] goodTypes = history.getCommodities();

        Arrays.sort(goodTypes, Comparator.comparing(ICommodity::getName));

        for (ICommodity commodity : goodTypes)
        {
            strListGood += commodity + "\n";
            Double price = history.prices.average(commodity, rounds);
            strListGoodPrices += String.format("%.2f\n", price);
            Double asks = history.asks.average(commodity, rounds);
            strListGoodAsks += ((asks.intValue())+"\n");
            Double bids = history.bids.average(commodity, rounds);
            strListGoodBids += (bids.intValue())+"\n";
            Double trades = history.trades.average(commodity, rounds);
            strListGoodTrades += (trades.intValue())+"\n";
            setarrStrListInventory.add(commodity + "\n\n");
        }

        List<String> agentClasses = new ArrayList<>();

        for(AgentSnapshot s : snapshot.getAgents())
            if(!agentClasses.contains(s.getClassName()))
                agentClasses.add(s.getClassName());

        for (String key : agentClasses)
        {
            List<Double> inventory = new ArrayList<Double>();
            for (ICommodity str : goodTypes)
            {
                inventory.add(0.0);
            }
            strListAgent += key + "\n";
            Double profit = history.profit.average(key,rounds);
            strListAgentProfit += String.format("%.2f\n", profit);

            int count = 0;
            double money = 0;
            for (AgentSnapshot a : snapshot.getAgents())
            {
                if (a.getClassName().compareTo(key) == 0)
                {
                    count++;
                    money += a.getMoney();
                    for (int lic = 0;lic < goodTypes.length; lic++)
                    {
                        inventory.add(lic, inventory.get(lic) + a.getInventory().query(goodTypes[lic]));
                    }
                }

            }
            money /= count;
            for (int lic = 0;lic < goodTypes.length; lic++)
            {
                inventory.add(lic, inventory.get(lic) / count);
                setarrStrListInventory.add(lic, setarrStrListInventory.get(lic) + String.format("%d\n", lic));
            }
            strListAgentCount += String.format("%d\n", count);
            strListAgentMoney += String.format("%d\n", (int)money);
        }

        return String.join("\n", new String[] {
                format_row(strListGood),
                format_row(strListGoodPrices),
                format_row(strListGoodTrades),
                format_row(strListGoodBids),
                format_row(strListGoodAsks),
        });
    }

}
