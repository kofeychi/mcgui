package dev.kofeychi.mcgui.impl.render.app;

import dev.kofeychi.mcgui.api.Event;
import dev.kofeychi.mcgui.api.render.app.Application;
import dev.kofeychi.mcgui.api.render.app.Window;
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;

import static dev.kofeychi.mcgui.api.render.Helpers.free;
import static org.lwjgl.glfw.GLFW.*;

public class WindowImpl implements Window {
    private final Int2IntAVLTreeMap hints = new Int2IntAVLTreeMap();
    private long handle;
    private boolean initialized = false;
    private final Application host;
    private String title;

    // event data

    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private int fwidth = 0;
    private int fheight = 0;

    // events

    private final Event<FramebufferSize> framebufferSizeEvent = Event.create(
            FramebufferSize.class,
            (arr) -> (w,h) -> {
                for(var l : arr) {
                    l.on(w,h);
                }
            }
    );
    private final Event<Position> positionEvent = Event.create(
            Position.class,
            (arr) -> (x,y) -> {
                for(var l : arr) {
                    l.on(x,y);
                }
            }
    );
    private final Event<Size> sizeEvent = Event.create(
            Size.class,
            (arr) -> (x,y) -> {
                for(var l : arr) {
                    l.on(x,y);
                }
            }
    );
    private final Event<Focus> focusEvent = Event.create(
            Focus.class,
            (arr) -> (s) -> {
                for(var l : arr) {
                    l.on(s);
                }
            }
    );
    private final Event<Iconify> iconifyEvent = Event.create(
            Iconify.class,
            (arr) -> (s) -> {
                for(var l : arr) {
                    l.on(s);
                }
            }
    );
    private final Event<Refresh> refreshEvent = Event.create(
            Refresh.class,
            (arr) -> () -> {
                for(var l : arr) {
                    l.on();
                }
            }
    );

    public WindowImpl(Application host) {
        this.host = host;
    }


    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public Application getHost() {
        return host;
    }

    @Override
    public Int2IntAVLTreeMap getHints() {
        return hints;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(handle, title);
    }

    @Override
    public void hint(int hint, int val) {
        hints.put(hint, val);
    }

    @Override
    public void init(long handle) {
        this.handle = handle;

        free(glfwSetFramebufferSizeCallback(handle,(window,w,h) -> framebufferSizeEvent.invoker().on(w,h)));
        free(glfwSetWindowPosCallback(handle,(window, xpos, ypos) -> positionEvent.invoker().on(xpos, ypos)));
        free(glfwSetWindowSizeCallback(handle,(window, width, height) -> sizeEvent.invoker().on(width, height)));
        free(glfwSetWindowFocusCallback(handle,(window, focused) -> focusEvent.invoker().on(focused)));
        free(glfwSetWindowIconifyCallback(handle,(window, iconify) -> iconifyEvent.invoker().on(iconify)));
        free(glfwSetWindowRefreshCallback(handle,(window)->refreshEvent.invoker().on()));

        framebufferSizeEvent.add((w,h)->{
            this.fwidth = w;
            this.fheight = h;
        });
        positionEvent.add((w,h)->{
            this.x = w;
            this.y = h;
        });
        sizeEvent.add((w,h)->{
            this.width = w;
            this.height = h;
        });

        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() {
        initialized = false;
        framebufferSizeEvent.clear();
        positionEvent.clear();
        sizeEvent.clear();
        focusEvent.clear();
        iconifyEvent.clear();
        refreshEvent.clear();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getFramebufferWidth() {
        return fwidth;
    }

    @Override
    public int getFramebufferHeight() {
        return fheight;
    }

    @Override public Event<FramebufferSize> framebufferSize() {return framebufferSizeEvent;}

    @Override public Event<Position> position() {return positionEvent;}

    @Override public Event<Size> size() {return sizeEvent;}

    @Override public Event<Focus> focus() {return focusEvent;}

    @Override public Event<Iconify> iconify() {return iconifyEvent;}

    @Override public Event<Refresh> refresh() {return refreshEvent;}
}
