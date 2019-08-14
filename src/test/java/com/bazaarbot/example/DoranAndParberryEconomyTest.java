package com.bazaarbot.example;

import com.bazaarbot.Economy;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.MarketReport;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

    @Test
    public void test_general() {
        String[] expected = new String[] {
                "Commodities    |Food           |Tools          |Wood           |Work           |Metal          |Ore            |",
                "Price          |0.26           |0.61           |0.52           |1.02           |0.40           |0.26           |",
                "Trades         |30             |1              |5              |5              |1              |4              |",
                "Demand         |30             |1              |5              |7              |1              |4              |",
                "Supply         |34             |30             |20             |5              |23             |28             |"
        };

        Random r = new Random(1234);
        Economy economy = new DoranAndParberryEconomy(r);
        Market market = economy.getMarket("default");

        market.simulate(20);
        MarketReport res = market.get_marketReport(1);

        String[] result = new String[] {
                res.strListGood,
                res.strListGoodPrices,
                res.strListGoodTrades,
                res.strListGoodBids,
                res.strListGoodAsks
        };
        for(int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], format_row(result[i]));
        }
    }
}
