package dev.kofeychi.mcgui.todo.render.buf;

import dev.kofeychi.mcgui.todo.render.buf.format.VertexFormat;
import org.lwjgl.system.MemoryUtil;

public record BuiltBuffer(long address, int sizeInBytes, int vertexCount, VertexFormat format, int mode) implements AutoCloseable {
    public void close() {
        MemoryUtil.nmemFree(address);
    }

    public Mesh toMesh() {
        return new Mesh(this);
    }
}
