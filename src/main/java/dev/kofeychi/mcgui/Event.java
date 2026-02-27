package dev.kofeychi.mcgui;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

public class Event<T> {
    private volatile T invoker;
    private final Function<T[], T> creator;
    private T[] handlers;
    @SuppressWarnings("unchecked")
    private Event(Class<? super T> type, Function<T[], T> creator) {
        this.creator = creator;
        this.handlers = (T[]) Array.newInstance(type, 0);
        this.invoker = creator.apply(handlers);
    }
    public void add(T listener) {
        this.handlers = Arrays.copyOf(handlers, handlers.length + 1);
        this.handlers[handlers.length - 1] = listener;
        this.invoker = creator.apply(handlers);
    }

    public T caller() {
        return invoker;
    }

    public static <T> Event<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
        return new Event<>(type, invokerFactory);
    }
}
