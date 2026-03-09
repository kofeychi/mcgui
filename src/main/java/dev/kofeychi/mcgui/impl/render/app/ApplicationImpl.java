package dev.kofeychi.mcgui.impl.render.app;

import dev.kofeychi.mcgui.api.render.app.Application;
import dev.kofeychi.mcgui.api.render.app.Render;

public class ApplicationImpl implements Application {
    private final Render render;

    public ApplicationImpl() {
        this.render = Render.standard(this);
    }

    @Override
    public Render getRender() {
        return render;
    }

    @Override
    public void init() {
        render.init();
    }

    @Override
    public void startLoop() {
        render.initLoop();
    }

    @Override
    public void close() {
        render.close();
    }
}
