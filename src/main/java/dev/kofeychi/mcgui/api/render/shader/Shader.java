package dev.kofeychi.mcgui.api.render.shader;

public interface Shader extends AutoCloseable {
    ShaderType getShaderType();

    void close();
}
