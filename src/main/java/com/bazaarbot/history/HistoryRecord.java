package com.bazaarbot.history;

/**
 * @author Nick Gritsenko
 */
class HistoryRecord<T> {

    private final T historyObject;


    HistoryRecord(T historyObject) {
        this.historyObject = historyObject;
    }

    T getHistoryObject() {
        return historyObject;
    }
}
