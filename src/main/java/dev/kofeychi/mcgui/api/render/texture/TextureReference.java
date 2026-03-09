package dev.kofeychi.mcgui.api.render.texture;

public interface TextureReference {
    TextureManager manager();

    int id();

    void bind(int id);
}
