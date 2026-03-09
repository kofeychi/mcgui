package dev.kofeychi.mcgui.impl.render.texture;

import dev.kofeychi.mcgui.api.render.texture.Filtering;
import dev.kofeychi.mcgui.api.render.texture.TextureOptions;

import static org.lwjgl.opengl.GL11.*;

public class TextureOptionsImpl implements TextureOptions {
    private Filtering min = Filtering.NEAREST,mag = Filtering.NEAREST;
    private int s = GL_REPEAT,t = GL_REPEAT;
    @Override
    public Filtering min() {
        return min;
    }

    @Override
    public Filtering mag() {
        return mag;
    }

    @Override
    public int wrapS() {
        return s;
    }

    @Override
    public int wrapT() {
        return t;
    }

    @Override
    public void setFilter(Filtering min, Filtering mag) {
        this.min=min;
        this.mag=mag;
    }

    @Override
    public void setWrap(int s, int t) {
        this.s=s;
        this.t=t;
    }

    @Override
    public void apply() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min.getGlType());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag.getGlType());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, s);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, t);
    }
}
