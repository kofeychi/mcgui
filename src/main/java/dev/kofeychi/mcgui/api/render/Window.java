package dev.kofeychi.mcgui.api.render;

public interface Window extends AutoCloseable {
    long getHandle();

    void init();

    boolean isInitialized();

    void close();
}
