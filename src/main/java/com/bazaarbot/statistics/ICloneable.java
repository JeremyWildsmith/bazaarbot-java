package com.bazaarbot.statistics;

/**
 * @author Nick Gritsenko
 */
public interface ICloneable<T> extends Cloneable {
    T clone();
}
