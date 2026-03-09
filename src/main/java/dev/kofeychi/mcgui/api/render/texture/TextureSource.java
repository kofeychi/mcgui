package dev.kofeychi.mcgui.api.render.texture;

import dev.kofeychi.mcgui.impl.render.texture.TextureSourceImpl;

import java.io.InputStream;

public interface TextureSource {
    static TextureSource fromStream(InputStream stream,TextureOptions options) {
        return new TextureSourceImpl(stream,options);
    }

    TextureOptions options();

    UploadedTexture upload();
}
