package com.bazaarbot;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static void format_row(String cols) {
        String row = "";

        for(String c : cols.split("\\s+")) {
            String working = c.trim();
            row += padRight(working, 15) + "|";
        }

        System.out.println(row);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        Economy economy = new DoranAndParberryEconomy();
        Market market = economy.getMarket("default");

        //dataGridView2.DataSource = res.arrStrListInventory;
        int rounds = 1;
        while(true) {

            market.simulate(1);
            MarketReport res = market.get_marketReport(1);
            System.out.println("\nRound: " + rounds);
            format_row(res.strListGood);
            format_row(res.strListGoodPrices);
            format_row(res.strListGoodTrades);
            format_row(res.strListGoodBids);
            format_row(res.strListGoodAsks);

            List<String> inventory = res.getarrStrListInventory();
            scanner.nextLine();

            rounds += 1;
        }
    }
}
