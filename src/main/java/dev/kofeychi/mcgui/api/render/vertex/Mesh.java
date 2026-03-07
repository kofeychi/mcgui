package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.impl.render.vertex.MeshImpl;

public interface Mesh extends AutoCloseable {
    static Mesh from(Built built) {
        return new MeshImpl(built);
    }

    int vao();

    int vbo();

    int vertexCount();

    int mode();

    void setMode(int mode);

    Built built();

    void upload();

    void draw();

    void close();
}
