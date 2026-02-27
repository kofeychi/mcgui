package dev.kofeychi.mcgui.render.shader;

import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public final int id;
    public final ShaderType type;
    public final Object2IntAVLTreeMap<String> uniforms = new Object2IntAVLTreeMap<>();

    public Shader(ShaderType type, String source) {
        this.type = type;
        this.id = glCreateProgram();
        int shader = compileShader(type, source);

        glAttachShader(id, shader);
        glLinkProgram(id);

        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader linking failed: " + glGetProgramInfoLog(id));
        }

        glDeleteShader(shader);
    }

    public void clean() {
        glDeleteProgram(id);
    }
    
    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int uniform(String uniform) {
        return uniforms.getInt(uniform);
    }

    public int compileShader(ShaderType type, String source) {
        int shader = glCreateShader(type.gltype);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shader));
        }
        return shader;
    }
}
