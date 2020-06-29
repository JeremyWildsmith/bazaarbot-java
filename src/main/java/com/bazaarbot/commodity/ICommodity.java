package com.bazaarbot.commodity;

public interface ICommodity extends Cloneable {
    String getName();

    //Amount of space consumed by commodity
    double getSpace();
}
