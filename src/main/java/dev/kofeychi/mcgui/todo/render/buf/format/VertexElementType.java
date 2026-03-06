package dev.kofeychi.mcgui.todo.render.buf.format;

import org.lwjgl.opengl.GL11;

public enum VertexElementType {
    FLOAT(4, GL11.GL_FLOAT),
    UBYTE(1, GL11.GL_UNSIGNED_BYTE),
    USHORT(2, GL11.GL_UNSIGNED_SHORT);

    public final int size;
    public final int glType;

    VertexElementType(int size, int glType) {
        this.size = size;
        this.glType = glType;
    }
}