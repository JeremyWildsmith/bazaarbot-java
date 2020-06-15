package com.bazaarbot.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ListenerRegistry {
    private List<Consumer<IEvent>> eventList = new CopyOnWriteArrayList<>();

    public synchronized <T extends IEvent> void addListener(Consumer<T> handler) {
        eventList.add((Consumer<IEvent>) handler);
    }

    public synchronized <T extends IEvent> void fire(T event) {
        eventList.stream()
                .filter((e) -> e.getClass().equals(event.getClass()))
                .forEach((e) -> e.accept(event));
    }
}
