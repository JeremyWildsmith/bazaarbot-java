package com.bazaarbot.example;

import com.bazaarbot.Economy;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.MarketSnapshot;
import com.bazaarbot.market.SimpleSummaryMarketReporter;
import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;

public class DoranAndParberryEconomyTest {

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String format_row(String cols) {
        String row = "";

        for(String c : cols.split("\\s+")) {
            String working = c.trim();
            row += padRight(working, 15) + "|";
        }

        return row;
    }

    public void test_general() {
        String expected =
                        "Commodities    |Food           |Metal          |Ore            |Tools          |Wood           |Work           |\n" +
                        "Price          |0.26           |0.44           |0.26           |0.57           |0.51           |1.02           |\n" +
                        "Trades         |28             |1              |5              |0              |5              |5              |\n" +
                        "Demand         |28             |1              |5              |0              |5              |5              |\n" +
                        "Supply         |38             |29             |25             |31             |20             |5              |";
        Random r = new Random(1234);
        Economy economy = new DoranAndParberryEconomy(r);
        Market market = economy.getMarket("default");

        market.simulate(20);
        SimpleSummaryMarketReporter reporter = new SimpleSummaryMarketReporter(market.getSnapshot());

        assertEquals(expected, reporter.produce(1));
    }
}
