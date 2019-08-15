package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.AgentSnapshot;
import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.history.History;
import com.bazaarbot.history.HistoryLog;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvMarketReporter {
    private final List<MarketSnapshot> snapshots = new ArrayList<>();
    private final Set<ICommodity> commodities = new HashSet<>();
    private final Set<String> agentClasses = new HashSet<>();

    public CsvMarketReporter() {
    }

    public void record(MarketSnapshot snapshot) {
        snapshots.add(snapshot);
        commodities.addAll(snapshot.getHistory().getCommodities());

        for(AgentSnapshot a : snapshot.getAgents()) {
            agentClasses.add(a.getClassName());
        }
    }

    private <T> String produceLogHeader(List<T> subjects) {
        StringBuilder csv = new StringBuilder();

        List<String> names = new ArrayList<>();
        names.add("Round#");

        for(T s : subjects) {
            names.add(s.toString());
        }

        return String.join(",", names);
    }

    private <T> String produceLogRecord(int round, HistoryLog<T> log, List<T> subjects) {
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(round));
        for(T s : subjects) {
            double avg = log.average(s, 1);
            values.add(String.valueOf(avg));
        }

        return String.join(",", values);
    }

    private <T> String produceLog(List<HistoryLog<T>> logs, List<T> subjects) {
        StringBuilder result = new StringBuilder();

        result.append(produceLogHeader(subjects)).append("\n");

        int round = 0;
        for(int i = 0; i < logs.size(); i++) {
            result.append(produceLogRecord(round++, logs.get(i), subjects)).append("\n");
        }

        return result.toString();
    }

    private void saveLog(String log, String content) throws IOException {
        BufferedWriter fos = new BufferedWriter(new FileWriter(log + ".csv"));
        fos.write(content);
        fos.close();
    }

    private String produceAgentMetric(String name, IAgentMetricFetcher fetcher) {
        List<String> rows = new ArrayList<>();
        rows.add("round#," + String.join(",", agentClasses));

        for(int i = 0; i < snapshots.size(); i++) {
            List<String> columns = new ArrayList<>();
            columns.add(String.valueOf(i));

            for (String key : agentClasses) {
                List<Double> inventory = new ArrayList<Double>();
                for (ICommodity str : commodities) {
                    inventory.add(0.0);
                }

                //Double metric = snapshots.get(i).getHistory().profit.average(key, 1);
                Double metric = fetcher.fetch(key, snapshots.get(i));
                columns.add(String.valueOf(metric));
            }
            rows.add(String.join(",", columns));
        }

        return String.join("\n", rows);
    }

    public void produce() throws IOException {
        List<ICommodity> goodTypes = new ArrayList<>(commodities);
        goodTypes.sort(Comparator.comparing(ICommodity::getName));

        String asks = this.produceLog(
                snapshots.stream().map(
                        b -> b.getHistory().asks
                ).collect(Collectors.toList()), goodTypes);

        String bids = this.produceLog(
                snapshots.stream().map(
                        b -> b.getHistory().bids
                ).collect(Collectors.toList()), goodTypes);

        String prices = this.produceLog(
                snapshots.stream().map(
                        b -> b.getHistory().prices
                ).collect(Collectors.toList()), goodTypes);

        String trades = this.produceLog(
                snapshots.stream().map(
                        b -> b.getHistory().trades
                ).collect(Collectors.toList()), goodTypes);

        String agentProfit = produceAgentMetric("profit", (name, snapshot) ->
                snapshot.getHistory().profit.average(name, 1)
        );

        String agentMoney = produceAgentMetric("money", (name, snapshot) -> {
            int count = 0;
            double money = 0;
            for (AgentSnapshot a : snapshot.getAgents()) {
                if (a.getClassName().compareTo(name) == 0) {
                    count++;
                    money += a.getMoney();
                }

            }
            money /= count;

            return money;
        });

        String agentCount = produceAgentMetric("agentCount", (name, snapshot) -> {
            int count = 0;
            for (AgentSnapshot a : snapshot.getAgents()) {
                if (a.getClassName().compareTo(name) == 0)
                    count++;
            }

            return count;
        });

        saveLog("asks", asks);
        saveLog("bids", bids);
        saveLog("prices", prices);
        saveLog("trades", trades);
        saveLog("profit", agentProfit);
        saveLog("money", agentMoney);
        saveLog("classCount", agentCount);
    }

    interface IAgentMetricFetcher {
        double fetch(String className, MarketSnapshot snapshot);
    }

}
