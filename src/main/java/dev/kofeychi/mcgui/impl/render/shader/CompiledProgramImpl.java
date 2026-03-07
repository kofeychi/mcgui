package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.shader.CompiledShader;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.shader.Uniforms;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class CompiledProgramImpl implements CompiledProgram {
    private final Object2ObjectOpenHashMap<String,Uniforms> uniforms = new Object2ObjectOpenHashMap<>();
    private final Program source;
    private final int id;

    public CompiledProgramImpl(Program source, int id) {
        this.source = source;
        this.id = id;
    }

    @Override
    public Program source() {
        return source;
    }

    @Override
    public List<CompiledShader> getShaders() {
        return source.getShaders();
    }

    @Override
    public Uniforms getUniforms(String name) {
        return uniforms.computeIfAbsent(
                name,
                n -> new UniformsImpl((String) n,id,glGetUniformLocation(id,name))
        );
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void close() {
        glDeleteProgram(id);
    }
}
