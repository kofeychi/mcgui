package dev.kofeychi.mcgui.impl.render;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.vertex.Builder;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import dev.kofeychi.mcgui.api.render.vertex.Mesh;

public class ContextCachedImpl extends ContextImpl {
    private Mesh cached;

    public ContextCachedImpl(BuilderSource source, CompiledProgram program) {
        super(source, program);
    }

    @Override
    public void draw() {
        if(cached == null) {
            cached = this.source.buildToMesh();
        }
        cached.draw();
    }

    @Override
    public void invalidateCache() {
        if(cached!=null){
            cached.close();
            cached = null;
        }
        source.close();
        source = Builder.from(256,program.format());
    }
}
