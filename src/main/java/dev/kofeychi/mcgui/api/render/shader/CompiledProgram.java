package dev.kofeychi.mcgui.api.render.shader;

import java.util.List;

import static org.lwjgl.opengl.GL20.glUseProgram;

public interface CompiledProgram extends AutoCloseable {
    Program source();

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
