package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.api.render.vertex.format.DrawMode;
import dev.kofeychi.mcgui.impl.render.vertex.MeshImpl;

public interface Mesh extends AutoCloseable {
    static Mesh from(Built built) {
        return new MeshImpl(built);
    }

    int vao();

    int vbo();

    int vertexCount();

    Built built();

    void upload();

    void draw();

    void close();

    DrawMode mode();

    void setMode(DrawMode mode);
}
