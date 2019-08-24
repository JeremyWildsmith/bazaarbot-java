package com.bazaarbot.history;

/**
 * @author Nick Gritsenko
 */
class HistoryRecord<T extends ICloneable<T>> {

    private final T historyObject;
    private final long timeCreated = System.nanoTime();

    HistoryRecord(T historyObject) {
        this.historyObject = historyObject.clone();
    }

    T getHistoryObject() {
        return historyObject;
    }

    long getTimeCreated() {
        return timeCreated;
    }

}
