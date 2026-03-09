package dev.kofeychi.mcgui.api.render.texture;

public interface UploadedTexture {
    int id();

    int width();

    int height();

    TextureSource source();

    void bind(int id);
}
