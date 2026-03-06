package dev.kofeychi.mcgui.api.render.shader;

import java.util.List;

public interface Program extends AutoCloseable {
    List<Shader> getShaders();

    void close();

    Uniforms getUniforms(String name);
}
