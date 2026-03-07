package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.shader.Program;

public interface BuilderSource extends AutoCloseable {
    Builder getBuilder(CompiledProgram program);

    void draw();

    void close();
}
