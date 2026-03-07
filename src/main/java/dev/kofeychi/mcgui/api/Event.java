package dev.kofeychi.mcgui.api;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

// highly stripped version of Fabricmc event system,credits to them.
public class Event<T> {
    private volatile T invoker;
    private final Function<T[], T> creator;
    private T[] handlers;
    private boolean valid = true;

    @SuppressWarnings("unchecked")
    private Event(Class<? super T> type, Function<T[], T> creator) {
        this.creator = creator;
        this.handlers = (T[]) Array.newInstance(type, 0);
        this.invoker = creator.apply(handlers);
    }

    public void add(T listener) {
        if(!valid) {
            throw new IllegalStateException("Tried to add listener to an event instance which has been forcibly closed");
        }
        this.handlers = Arrays.copyOf(handlers, handlers.length + 1);
        this.handlers[handlers.length - 1] = listener;
        this.invoker = creator.apply(handlers);
    }

    public T invoker() {
        return invoker;
    }

    public void clear() {
        invoker = null;
        handlers = null;
        valid = false;
    }

    public static <T> Event<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
        return new Event<>(type, invokerFactory);
    }
}
