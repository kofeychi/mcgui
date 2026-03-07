package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.CompiledShader;
import dev.kofeychi.mcgui.api.render.shader.Shader;

public class CompiledShaderImpl implements CompiledShader {
    private final Shader shader;
    private final int id;

    public CompiledShaderImpl(Shader shader, int id) {
        this.shader = shader;
        this.id = id;
    }

    @Override
    public Shader source() {
        return shader;
    }

    @Override
    public int id() {
        return id;
    }
}
