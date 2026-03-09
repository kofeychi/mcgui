package dev.kofeychi.mcgui.api.render.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;

public enum Filtering {
    NEAREST(GL_NEAREST),
    LINEAR(GL_LINEAR);

    private final int glType;

    Filtering(int glType) {
        this.glType = glType;
    }

    public int getGlType() {
        return glType;
    }
}
