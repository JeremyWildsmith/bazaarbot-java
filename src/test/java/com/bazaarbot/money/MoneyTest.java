package com.bazaarbot.money;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MoneyTest {

    private List<Double> doubles1 = new ArrayList<>();
    private List<BigDecimal> decimals1 = new ArrayList<>();
    private List<Double> doubles2 = new ArrayList<>();
    private List<BigDecimal> decimals2 = new ArrayList<>();

    @Test
    public void testMoney() {
        Random random = new Random();
        random.doubles(1_000)
                .forEach((d) -> {
                    decimals1.add(BigDecimal.valueOf(d));
                    doubles1.add(d);
                });
        random.doubles(1_000)
                .forEach((d) -> {
                    decimals2.add(BigDecimal.valueOf(d));
                    doubles2.add(d);
                });
        double error = 0d;
        for (int i=0; i< decimals1.size(); i++) {
            BigDecimal b1 = decimals1.get(i);
            BigDecimal b2 = decimals2.get(i);

            var d1 = b1.add(b2).setScale(4, RoundingMode.HALF_UP).doubleValue();

            Money m1 = Money.of(doubles1.get(i));
            Money m2 = Money.of(doubles2.get(i));

            var d2 = m1.add(m2).getDoubleAmount();

            if (Double.compare(d1, d2) != 0) {
                error += Math.abs(d1-d2);
                System.err.println(d1 + " " + d2);
            }
        }
        System.err.println("Error " + error);
    }

}
