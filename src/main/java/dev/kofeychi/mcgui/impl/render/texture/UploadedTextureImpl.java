package dev.kofeychi.mcgui.impl.render.texture;

import dev.kofeychi.mcgui.api.render.texture.TextureSource;
import dev.kofeychi.mcgui.api.render.texture.UploadedTexture;

import static org.lwjgl.opengl.GL13.*;

public class UploadedTextureImpl implements UploadedTexture {
    private final TextureSource source;
    private final int id,w,h;

    public UploadedTextureImpl(TextureSource source, int id, int w, int h) {
        System.out.println(id);
        this.source = source;
        this.id = id;
        this.w = w;
        this.h = h;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int width() {
        return w;
    }

    @Override
    public int height() {
        return h;
    }

    @Override
    public TextureSource source() {
        return source;
    }

    @Override
    public void bind(int id) {
        glActiveTexture(id);
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
