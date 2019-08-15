package com.bazaarbot.example;

import com.bazaarbot.Economy;
import com.bazaarbot.market.Market;
import com.bazaarbot.market.SimpleSummaryMarketReporter;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Economy economy = new DoranAndParberryEconomy(new Random(1234));
        Market market = economy.getMarket("default");

        int rounds = 1;
        while(true) {

            market.simulate(1);
            SimpleSummaryMarketReporter reporter = new SimpleSummaryMarketReporter(market.getSnapshot());
            System.out.println("\nRound: " + rounds);
            System.out.println(reporter.produce(1));

            try {
                scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }
            rounds += 1;
        }
    }
}
