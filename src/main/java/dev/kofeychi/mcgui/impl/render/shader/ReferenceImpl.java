package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.shader.Manager;
import dev.kofeychi.mcgui.api.render.shader.Reference;
import dev.kofeychi.mcgui.api.render.shader.Uniforms;

public class ReferenceImpl implements Reference {
    private final int programId;
    private final Manager manager;

    public ReferenceImpl(int programId, Manager manager) {
        this.programId = programId;
        this.manager = manager;
    }

    @Override
    public void bind() {
        getProgram().bind();
    }

    @Override
    public void unbind() {
        getProgram().unbind();
    }

    @Override
    public CompiledProgram getProgram() {
        return manager.get(programId);
    }

    @Override
    public Manager getManager() {
        return manager;
    }

    @Override
    public int programId() {
        return programId;
    }

    @Override
    public Uniforms getUniforms(String name) {
        return getProgram().getUniforms(name);
    }
}
