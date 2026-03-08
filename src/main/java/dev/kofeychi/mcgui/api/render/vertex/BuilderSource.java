package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.impl.render.vertex.BuilderSourceImpl;

public interface BuilderSource extends AutoCloseable {
    static BuilderSource create() {
        return new BuilderSourceImpl();
    }

    Builder getBuilder(CompiledProgram program);

    void draw();

    void close();

    void close(Builder builder);
}
