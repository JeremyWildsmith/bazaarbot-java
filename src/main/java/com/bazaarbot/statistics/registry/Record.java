package com.bazaarbot.statistics.registry;

import com.bazaarbot.statistics.ICloneable;

/**
 * @author Nick Gritsenko
 */
class Record<T extends ICloneable<T>> {

    private final T historyObject;
    private final long timeCreated = System.nanoTime();

    Record(T historyObject) {
        this.historyObject = historyObject.clone();
    }

    T getHistoryObject() {
        return historyObject;
    }

    long getTimeCreated() {
        return timeCreated;
    }

}
