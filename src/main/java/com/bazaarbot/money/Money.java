package com.bazaarbot.money;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money implements Comparable<Money> {
    private static final Logger LOG = LoggerFactory.getLogger(Money.class);
    private static final int SCALE = 1_0000;
    private static final short FRACTIONAL_PART_COUNT = 4;

    private long moneyAmount = 0L;


    private Money(double number) {
        this.moneyAmount = convertToLong(number);
    }

    private Money(BigDecimal number) {
        this.moneyAmount = convertToLong(number);
    }

    public static Money of(double amount) {
        return new Money(amount);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public double getDoubleAmount() {
        return (double) moneyAmount / SCALE;
    }

    public long getLongAmount() {
        return moneyAmount;
    }

    public Money add(BigDecimal bigDecimal) {
        long amount = convertToLong(bigDecimal);
        return new Money(amount + moneyAmount);
    }


    public Money add(Money money) {
        return new Money(money.getLongAmount() + moneyAmount);
    }

    public Money add(double amount) {
        long longAmount = convertToLong(amount);
        return new Money(longAmount + moneyAmount);
    }

    public Money subtract(Money money) {
        return subtract(money.getDoubleAmount());
    }

    public Money subtract(double amount) {
        long longAmount = convertToLong(amount);
        long totalMoneyAmount = moneyAmount - longAmount;
        if (totalMoneyAmount < 0) {
            LOG.warn("< than zero");
        }
        return new Money(totalMoneyAmount);
    }


//    public static void main(String[] args) {
//        System.err.println(convertToLong(2222.331549));
//    }

    private long convertToLong(BigDecimal bigDecimal) {
        return bigDecimal.setScale(FRACTIONAL_PART_COUNT, RoundingMode.HALF_UP).longValue();
    }

    private long convertToLong(double amount) {
        int count = 0;
        while (amount % 1 != 0) {
            count++;
            if (count > FRACTIONAL_PART_COUNT) {
                break;
            }
            amount *= 10;
        }
        return Math.round(amount);
    }

    public Money multiply(Money money) {
        return multiply(money.getDoubleAmount());
    }

    public Money multiply(double amount) {
        long longAmount = convertToLong(amount);
        return Money.of(longAmount * moneyAmount);
    }

    @Override
    public int compareTo(Money money) {
        return Long.compare(moneyAmount, money.getLongAmount());
    }
}
