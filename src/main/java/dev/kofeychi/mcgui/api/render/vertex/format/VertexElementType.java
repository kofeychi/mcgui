package dev.kofeychi.mcgui.api.render.vertex.format;

import static org.lwjgl.opengl.GL11.*;

public enum VertexElementType {
    FLOAT(4, GL_FLOAT),
    UBYTE(1, GL_UNSIGNED_BYTE),
    BYTE(1, GL_BYTE),
    USHORT(2, GL_UNSIGNED_SHORT),
    SHORT(2, GL_SHORT),
    UINT(4, GL_UNSIGNED_INT),
    INT(4, GL_INT),;

    public final int size;
    public final int glType;

    VertexElementType(int size, int glType) {
        this.size = size;
        this.glType = glType;
    }
}