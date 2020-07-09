package com.bazaarbot.money;

public class Money2 {
    private long moneyAmount = 0L;

    private Money2(double amount) {
        this.moneyAmount = (long) (amount * 100);
    }

    public static Money2 of(double amount) {
        return new Money2(amount);
    }

    public double getAmount() {
        return (double) Math.round(moneyAmount * 100) / 100;
    }

    public Money2 add(double amount) {
        long longAmount = (long) (amount * 100);
        this.moneyAmount += longAmount;
        return this;
    }

    public Money2 subtract(double amount) {
        long longAmount = (long) (amount * 100);
        this.moneyAmount -= longAmount;
        return this;
    }

    public Money2 multiply(double amount) {
        long longAmount = (long) (amount * 100);
        this.moneyAmount *= longAmount;
        return this;
    }

    public Money2 divide(double amount) {
        long longAmount = (long) (amount * 100);
        this.moneyAmount /= longAmount;
        return this;
    }
}
