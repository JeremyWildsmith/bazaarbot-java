package com.bazaarbot.events;

/**
 * @author Nick Gritsenko
 */
public interface IEventHandler<T> {
    void handle(T eventObject);
}
