package net.cosmos.event;

import java.util.*;
import java.util.function.Consumer;

public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> type, Consumer<T> handler) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>())
                 .add((Consumer<Object>) handler);
    }

    public <T> void post(T event) {
        List<Consumer<Object>> list = listeners.get(event.getClass());
        if (list != null) for (Consumer<Object> c : list) c.accept(event);
    }
}
