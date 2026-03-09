package dev.kofeychi.mcgui.api.render.texture;


import dev.kofeychi.mcgui.impl.render.texture.TextureManagerImpl;

public interface TextureManager extends AutoCloseable {
    static TextureManager empty() {
        return new TextureManagerImpl();
    }

    TextureReference load(String name, TextureSource tex);

    UploadedTexture get(int id);

    UploadedTexture get(String name);

    void close();
}

