package dev.kofeychi.mcgui.api.render.shader;

public interface Reference {
    void bind();

    void unbind();

    Uniforms getUniforms(String name);
}
