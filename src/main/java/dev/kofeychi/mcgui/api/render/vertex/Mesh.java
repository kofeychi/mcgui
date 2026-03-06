package dev.kofeychi.mcgui.api.render.vertex;

public interface Mesh extends AutoCloseable {
    boolean isUploaded();

    void upload();

    void draw();

    void close();
}
