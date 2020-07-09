package com.bazaarbot.money;

public enum Precision {
    OF_ONE(10),
    OF_TWO(100),
    OF_THREE(1000),
    OF_FOUR(10_000);

    private final int precision;
    Precision(int precision) {
        this.precision = precision;
    }

    public int get() {
        return precision;
    }
}
