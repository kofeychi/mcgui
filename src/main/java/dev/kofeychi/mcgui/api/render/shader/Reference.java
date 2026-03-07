package dev.kofeychi.mcgui.api.render.shader;

public interface Reference {
    void bind();

    void unbind();

    CompiledProgram getProgram();

    Manager getManager();

    int programId();

    Uniforms getUniforms(String name);
}
