package com.bazaarbot;

public class Main {
    public static void main(String[] args) throws Exception {

        Economy economy = new DoranAndParberryEconomy();
        Market market = economy.getMarket("default");

        //dataGridView2.DataSource = res.arrStrListInventory;
        int rounds = 0;
        while(true) {

            market.simulate(20);
            MarketReport res = market.get_marketReport(20);
            System.out.println("res.strListGood\n" + res.strListGood);
            System.out.println("res.strListGoodPrices\n" + res.strListGoodPrices);
            System.out.println("res.strListGoodTrades\n" + res.strListGoodTrades);
            System.out.println("res.strListGoodBids\n" + res.strListGoodBids);
            System.out.println("res.strListGoodAsks\n" + res.strListGoodAsks);

            System.in.read();

            rounds += 1;
        }
    }
}
