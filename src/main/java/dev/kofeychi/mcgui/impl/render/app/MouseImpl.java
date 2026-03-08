package dev.kofeychi.mcgui.impl.render.app;

import dev.kofeychi.mcgui.api.Event;
import dev.kofeychi.mcgui.api.render.app.Mouse;
import dev.kofeychi.mcgui.api.render.app.Render;
import org.joml.Vector2d;
import org.lwjgl.system.MemoryStack;

import static dev.kofeychi.mcgui.api.render.Helpers.free;
import static dev.kofeychi.mcgui.api.render.Helpers.lerp;
import static org.lwjgl.glfw.GLFW.*;

public class MouseImpl implements Mouse {
    private double x = 0;
    private double y = 0;
    private double prevx = 0;
    private double prevy = 0;
    private final Render host;

    public MouseImpl(Render host) {
        this.host = host;
    }

    // events

    private final Event<Position> positionEvent = Event.create(
            Position.class,
            (arr) -> (x,y) -> {
                for(var l : arr) {
                    l.on(x,y);
                }
            }
    );
    private final Event<Scroll> scrollEvent = Event.create(
            Scroll.class,
            (arr) -> (x,y) -> {
                for(var l : arr) {
                    l.on(x,y);
                }
            }
    );
    private final Event<Button> buttonEvent = Event.create(
            Button.class,
            (arr) -> (a,b,c) -> {
                for(var l : arr) {
                    l.on(a,b,c);
                }
            }
    );
    @Override
    public void init(long handle) {
        free(glfwSetCursorPosCallback(handle,(window,x,y)->positionEvent.invoker().on(x,y)));
        free(glfwSetScrollCallback(handle, (window,x,y)->scrollEvent.invoker().on(x,y)));
        free(glfwSetMouseButtonCallback(handle,(w,a,b,c)->buttonEvent.invoker().on(a,b,c)));
        positionEvent.add((x,y)->{
            this.prevx = this.x;
            this.prevy = this.y;
            this.x = x;
            this.y = y;
        });
    }

    @Override
    public void close(long window) {
        free(glfwSetCursorPosCallback(window,null));
        free(glfwSetMouseButtonCallback(window,null));
        free(glfwSetScrollCallback(window,null));
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double lerpedX(double delta) {
        return lerp(delta,prevx,x);
    }

    @Override
    public double lerpedY(double delta) {
        return lerp(delta,prevy,y);
    }

    @Override
    public Vector2d lerped(double delta) {
        return new Vector2d(lerpedX(delta),lerpedY(delta));
    }

    @Override
    public Vector2d glfwget() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var x = stack.mallocDouble(1);
            var y = stack.mallocDouble(1);
            glfwGetCursorPos(host.getWindow().getHandle(), x, y);
            var cx = x.get(0);
            var cy = y.get(0);

            return inBounds(cx,cy,host.getWindow().getFramebufferWidth(),host.getWindow().getFramebufferHeight())?new Vector2d(cx,cy):new Vector2d();
        }
    }

    private boolean inBounds(double x, double y,double w,double h) {
        return x >= 0 && x < w && y >= 0 && y < h;
    }

    @Override
    public Event<Position> position() {
        return positionEvent;
    }

    @Override
    public Event<Scroll> scroll() {
        return scrollEvent;
    }

    @Override
    public Event<Button> button() {
        return buttonEvent;
    }
}
