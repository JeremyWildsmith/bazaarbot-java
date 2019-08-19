//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryLog<T> {
    private EconNoun type = EconNoun.Price;
    private HashMap<T, ArrayList<Double>> log;

    public HistoryLog(HistoryLog<T> source) {
        this.type = source.type;
        log = new HashMap<>();
        for (Map.Entry<T, ArrayList<Double>> e : source.log.entrySet()) {
            log.put(e.getKey(), new ArrayList<>(e.getValue()));
        }
    }

    public HistoryLog(EconNoun type) {
        this.type = type;
        log = new HashMap<>();
    }

    /**
     * Add a new entry to this log
     *
     * @param name
     * @param amount
     */
    public void add(T name, double amount) {
        if (log.containsKey(name)) {
            List<Double> list = log.get(name);
            list.add(amount);
        }

    }

    /**
     * Register a new category list in this log
     *
     * @param name
     */
    public void register(T name) {
        if (!log.containsKey(name)) {
            log.put(name, new ArrayList<>());
        }

    }

    /**
     * Returns the average amount of the given category, looking backwards over a specified range
     *
     * @param name  the category of thing
     * @param range how far to look back
     * @return
     */
    public double average(T name, int range) {
        if (log.containsKey(name)) {
            List<Double> list = log.get(name);
            double amount = 0.0;
            int length = list.size();
            if (length < range) {
                range = length;
            }

            for (int i = 0; i < range; i++) {
                amount += list.get(length - 1 - i);
            }
            if (range <= 0)
                return -1;

            return amount / range;
        }

        return 0;
    }

    public List<T> getSubjects() {
        return new ArrayList<>(log.keySet());
    }
}


