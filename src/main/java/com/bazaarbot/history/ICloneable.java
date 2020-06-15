package com.bazaarbot.history;

/**
 * @author Nick Gritsenko
 */
public interface ICloneable<T> extends Cloneable {
    T clone();
}
