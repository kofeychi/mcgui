package dev.kofeychi.mcgui.api.render.shader;

import dev.kofeychi.mcgui.impl.render.shader.ShaderImpl;

public interface Shader {
    static Shader from(ShaderType type,String source) {
        return new ShaderImpl(type, source);
    }

    ShaderType getShaderType();

    CompiledShader compile();

    String getSource();
}
