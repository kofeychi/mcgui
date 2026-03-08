package dev.kofeychi.mcgui.api.render.app;

import dev.kofeychi.mcgui.api.Event;
import dev.kofeychi.mcgui.impl.render.app.WindowImpl;
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;

public interface Window extends AutoCloseable {
    static Window empty(Application host) {
        return new WindowImpl(host);
    }

    long getHandle();

    Application getHost();

    Int2IntAVLTreeMap getHints();

    String getTitle();

    void setTitle(String title);

    void hint(int hint,int val);

    void init(long handle);

    boolean isInitialized();

    void close();

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getFramebufferWidth();

    int getFramebufferHeight();

    // events

    Event<FramebufferSize> framebufferSize();

    Event<Position> position();

    Event<Size> size();

    Event<Focus> focus();

    Event<Iconify> iconify();

    Event<Refresh> refresh();


    interface FramebufferSize {
        void on(int w, int h);
    }
    interface Position {
        void on(int x,int y);
    }
    interface Size {
        void on(int w,int h);
    }
    interface Focus {
        void on(boolean focus);
    }
    interface Iconify {
        void on(boolean iconified);
    }
    interface Refresh {
        void on();
    }
}
