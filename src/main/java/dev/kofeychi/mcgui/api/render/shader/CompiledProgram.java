package dev.kofeychi.mcgui.api.render.shader;

import dev.kofeychi.mcgui.api.render.vertex.format.Format;

import java.util.List;

import static org.lwjgl.opengl.GL20.glUseProgram;

public interface CompiledProgram extends AutoCloseable {
    Program source();

    Format format();

    List<CompiledShader> getShaders();

    Uniforms getUniforms(String name);

    int id();

    default void bind() {
        glUseProgram(id());
    }

    default void unbind() {
        glUseProgram(0);
    }

    void close();
}
