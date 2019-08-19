package com.bazaarbot.example;

import com.bazaarbot.Economy;
import com.bazaarbot.market.Market;
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

        for (String c : cols.split("\\s+")) {
            String working = c.trim();
            row += padRight(working, 15) + "|";
        }

        return row;
    }

    @Test
    public void test_general() {
        String expected =
                "Commodities    |Food           |Metal          |Ore            |Tools          |Wood           |Work           |\n" +
                        "Price          |0.26           |0.40           |0.26           |0.61           |0.52           |1.02           |\n" +
                        "Trades         |30             |1              |4              |1              |5              |5              |\n" +
                        "Demand         |30             |1              |4              |1              |5              |7              |\n" +
                        "Supply         |34             |23             |28             |30             |20             |5              |";
        Random r = new Random(1234);
        Economy economy = new DoranAndParberryEconomy(r);
        Market market = economy.getMarket("default");

        market.simulate(20);
        SimpleSummaryMarketReporter reporter = new SimpleSummaryMarketReporter(market.getSnapshot());

        assertEquals(expected, reporter.produce(1));
    }
}
