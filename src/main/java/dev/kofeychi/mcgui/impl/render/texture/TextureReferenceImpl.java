package dev.kofeychi.mcgui.impl.render.texture;

import dev.kofeychi.mcgui.api.render.texture.TextureManager;
import dev.kofeychi.mcgui.api.render.texture.TextureReference;

public class TextureReferenceImpl implements TextureReference {
    private final TextureManager manager;
    private final int id;

    public TextureReferenceImpl(TextureManager manager, int id) {
        this.manager = manager;
        this.id = id;
    }

    @Override
    public TextureManager manager() {
        return manager;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void bind(int id) {
        manager.get(id()).bind(id);
    }
}
