package dev.kofeychi.mcgui.api.render.app;

import dev.kofeychi.mcgui.api.Event;
import dev.kofeychi.mcgui.api.render.FpsController;
import dev.kofeychi.mcgui.api.render.shader.Manager;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import dev.kofeychi.mcgui.impl.render.app.RenderImpl;

public interface Render extends Logic,AutoCloseable {
    static Render standard(Application host) {
        return new RenderImpl(host);
    }

    void initLoop();

    void close();

    BuilderSource getSharedSource();

    Manager getShaderManager();

    FpsController getFpsController();

    Application getHost();

    Window getWindow();

    Mouse getMouse();

    Event<SetupRender> setupRender();

    Event<PostRender> postRender();

    Event<Close> onClose();

    interface SetupRender {
        void setup(Render host);
    }
    interface PostRender {
        void post(Render host);
    }
    interface Close {
        void close(Render host);
    }
}
