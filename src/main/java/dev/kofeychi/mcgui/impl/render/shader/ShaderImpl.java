package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.CompiledShader;
import dev.kofeychi.mcgui.api.render.shader.Shader;
import dev.kofeychi.mcgui.api.render.shader.ShaderType;
import dev.kofeychi.mcgui.api.render.vertex.format.Format;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderImpl implements Shader {
    private final ShaderType type;
    private final String source;

    public ShaderImpl(ShaderType type, String source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public ShaderType getShaderType() {
        return type;
    }

    @Override
    public CompiledShader compile() {
        int s = glCreateShader(type.glType()); glShaderSource(s, source); glCompileShader(s);
        if (glGetShaderi(s, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader ("+type.name()+") compilation failed: " + glGetShaderInfoLog(s));
        }
        return new CompiledShaderImpl(this,s);
    }

    @Override
    public String getSource() {
        return source;
    }
}
