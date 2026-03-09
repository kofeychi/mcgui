package dev.kofeychi.mcgui.api.render.app;

import dev.kofeychi.mcgui.api.Event;
import dev.kofeychi.mcgui.impl.render.app.MouseImpl;
import org.joml.Vector2d;

public interface Mouse extends AutoCloseable {
    static Mouse create(Render host) {
        return new MouseImpl(host);
    }

    void init(long window);

    void close();

    double getX();

    double getY();

    double lerpedX(double delta);

    double lerpedY(double delta);

    Vector2d lerped(double delta);

    Vector2d glfwget();

    // events

    Event<Position> position();

    Event<Scroll> scroll();

    Event<Button> button();

    interface Position {
        void on(double x, double y);
    }
    interface Scroll {
        void on(double x, double y);
    }
    interface Button {
        void on(int button,int action,int mod);
    }
}
