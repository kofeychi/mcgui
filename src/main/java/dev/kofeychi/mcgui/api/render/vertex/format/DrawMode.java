package dev.kofeychi.mcgui.api.render.vertex.format;

import static org.lwjgl.opengl.GL11.*;

public enum DrawMode {
    LINES(GL_LINES),
    LINE_STRIP(GL_LINE_STRIP),
    TRIANGLES(GL_TRIANGLES),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN),
    QUADS(GL_QUADS),;

    private final int glType;

    DrawMode(int glType) {
        this.glType = glType;
    }

    public int glType() {
        return glType;
    }
}
