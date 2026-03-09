package dev.kofeychi.mcgui.api.render;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import dev.kofeychi.mcgui.impl.render.ContextCachedImpl;
import dev.kofeychi.mcgui.impl.render.ContextImpl;
import dev.kofeychi.mcgui.util.Color;

public interface Context {
    static Context from(BuilderSource source, CompiledProgram program) {
        return new ContextImpl(source,program);
    }

    static Context cached(BuilderSource source, CompiledProgram program) {
        return new ContextCachedImpl(source,program);
    }

    MatrixStack getMatrices();

    void fill(int x, int y, int x1, int y1,int z,int c);

    void fill(int x, int y, int x1, int y1,int z, Color c);

    void texture(int x, int y, int x1, int y1,int z,int u,int v,int u1,int v1);

    void draw();

    void invalidateCache();
}
