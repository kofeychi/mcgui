package dev.kofeychi.mcgui.api.render.vertex;

public interface Built extends AutoCloseable {
    Mesh toMesh();

    void close();
}
