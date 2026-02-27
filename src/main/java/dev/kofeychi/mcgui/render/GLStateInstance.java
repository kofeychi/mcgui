package dev.kofeychi.mcgui.render;

import org.lwjgl.opengl.GL46;

public class GLStateInstance {
    private final int id;
    private boolean state;

    public GLStateInstance(int id) {
        this.id = id;
    }

    public void setState(boolean state) {
        this.state = state;
        if(state) {
            GL46.glEnable(id);
        }  else {
            GL46.glDisable(id);
        }
    }

    public boolean getState() {
        return state;
    }
}
