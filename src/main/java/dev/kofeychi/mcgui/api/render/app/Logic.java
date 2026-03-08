package dev.kofeychi.mcgui.api.render.app;

import dev.kofeychi.mcgui.api.Event;

import java.util.function.DoubleSupplier;
import java.util.function.Function;

public interface Logic {
    void init();

    void setTargetUps(Function<Application,DoubleSupplier> targetUps);

    Event<Update> update();

    Event<PostInit> postInit();

    Event<PreInit> preInit();

    interface Update {
        void on(Application application);
    }
    interface PostInit {
        void on(Application application);
    }
    interface PreInit {
        void on(Application application);
    }
}
