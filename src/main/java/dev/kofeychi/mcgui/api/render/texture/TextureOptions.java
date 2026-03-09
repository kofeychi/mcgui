package dev.kofeychi.mcgui.api.render.texture;

import dev.kofeychi.mcgui.impl.render.texture.TextureOptionsImpl;

public interface TextureOptions {
    static TextureOptions defaultOptions() {
        return new TextureOptionsImpl();
    }

    Filtering min();

    Filtering mag();

    int wrapS();

    int wrapT();

    void setFilter(Filtering min,Filtering mag);

    void setWrap(int s,int t);

    void apply();
}
