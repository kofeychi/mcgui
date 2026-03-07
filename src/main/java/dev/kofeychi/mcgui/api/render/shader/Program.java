package dev.kofeychi.mcgui.api.render.shader;

import dev.kofeychi.mcgui.impl.render.shader.ProgramImpl;

import java.util.Collection;
import java.util.List;

public interface Program {
    static Program from(Collection<Shader> shaders) {
        return new ProgramImpl(shaders.stream().map(Shader::compile).toList());
    }

    List<CompiledShader> getShaders();

    CompiledProgram compile();
}
