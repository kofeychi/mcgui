package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.shader.CompiledShader;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.vertex.format.Format;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class ProgramImpl implements Program {
    private final ObjectArrayList<CompiledShader> shaders;
    private final Format format;

    public ProgramImpl(Collection<CompiledShader> shaders, Format format) {
        this.shaders = new ObjectArrayList<>(shaders);
        this.format = format;
    }

    @Override
    public List<CompiledShader> getShaders() {
        return shaders;
    }

    @Override
    public CompiledProgram compile() {
        var id = glCreateProgram();
        for (CompiledShader shader : shaders) {
            glAttachShader(id,shader.id());
        }
        glLinkProgram(id);
        for (CompiledShader shader : shaders) {
            glDeleteShader(shader.id());
        }
        return new CompiledProgramImpl(this, id,format);
    }
}
