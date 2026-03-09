package dev.kofeychi.mcgui.api.render.app;

import dev.kofeychi.mcgui.impl.render.app.ApplicationImpl;

public interface Application extends AutoCloseable {
    static Application basic() {
        return new ApplicationImpl();
    }

    Render getRender();

    void init();

    void startLoop();

    void close();

}
