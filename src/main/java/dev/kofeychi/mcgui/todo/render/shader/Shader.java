package dev.kofeychi.mcgui.todo.render.shader;

import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public final int id;
    public final Object2IntAVLTreeMap<String> uniforms = new Object2IntAVLTreeMap<>();

    public Shader(String svert,String sfrag) {
        int vs = glCreateShader(GL_VERTEX_SHADER); glShaderSource(vs, svert); glCompileShader(vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER); glShaderSource(fs, sfrag); glCompileShader(fs);
        if (glGetShaderi(vs, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(vs));
        }
        if (glGetShaderi(fs, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(fs));
        }
        this.id = glCreateProgram();
        glAttachShader(id, vs); glAttachShader(id, fs); glLinkProgram(id);
        glDeleteShader(vs); glDeleteShader(fs);
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

    public void setUniformf1(String uniform,float value) {
        glUniform1f(uniforms.computeIfAbsent(uniform, k -> glGetUniformLocation(id, (String)k)), value);
    }
}
