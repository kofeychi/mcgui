package dev.kofeychi.mcgui.impl.render.texture;

import dev.kofeychi.mcgui.api.render.texture.TextureManager;
import dev.kofeychi.mcgui.api.render.texture.TextureReference;
import dev.kofeychi.mcgui.api.render.texture.TextureSource;
import dev.kofeychi.mcgui.api.render.texture.UploadedTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class TextureManagerImpl implements TextureManager {
    private final Object2ObjectOpenHashMap<String, UploadedTexture> byName = new Object2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<UploadedTexture> byId = new Int2ObjectOpenHashMap<>();

    @Override
    public TextureReference load(String name, TextureSource tex) {
        var c = tex.upload();
        byName.put(name, c);
        byId.put(c.id(), c);
        return new TextureReferenceImpl(this,c.id());
    }

    @Override
    public UploadedTexture get(int id) {
        return byId.get(id);
    }

    @Override
    public UploadedTexture get(String name) {
        return byName.get(name);
    }

    @Override
    public void close() {
        for (UploadedTexture t : byName.values()) {
            glDeleteTextures(t.id());
        }
        byId.clear();
        byName.clear();
    }
}
