package dev.kofeychi.mcgui.api.render;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import dev.kofeychi.mcgui.impl.render.ContextImpl;
import dev.kofeychi.mcgui.util.Color;

public interface Context {
    static Context from(BuilderSource source, CompiledProgram program) {
        return new ContextImpl(source,program);
    }
    MatrixStack getMatrices();

    void fill(int x, int y, int x1, int y1,int z,int c);

    void fill(int x, int y, int x1, int y1,int z, Color c);

    void draw();
}
