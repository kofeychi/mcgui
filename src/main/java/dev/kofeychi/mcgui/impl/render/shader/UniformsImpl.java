package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.Uniforms;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class UniformsImpl implements Uniforms {
    private final String name;
    private final int program;
    private final int location;

    public UniformsImpl(String name, int program, int location) {
        this.name = name;
        this.program = program;
        this.location = location;
        if(location < 0) {
            throw new RuntimeException("Unknown location");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int programId() {
        return program;
    }

    @Override
    public int location() {
        return location;
    }

    @Override
    public void setInt(int value) {
        glUniform1i(location, value);
    }

    @Override
    public void setFloat(float value) {
        glUniform1f(location, value);
    }

    @Override
    public void setIntVector2(int x, int y) {
        glUniform2i(location, x, y);
    }

    @Override
    public void setFloatVector2(float x, float y) {
        glUniform2f(location, x, y);
    }

    @Override
    public void setIntVector3(int x, int y, int z) {
        glUniform3i(location, x, y, z);
    }

    @Override
    public void setFloatVector3(float x, float y, float z) {
        glUniform3f(location, x, y, z);
    }

    @Override
    public void setIntVector4(int x, int y, int z, int w) {
        glUniform4i(location, x, y, z, w);
    }

    @Override
    public void setFloatVector4(float x, float y, float z, int w) {
        glUniform4f(location, x, y, z, w);
    }

    @Override
    public void setFloatMat4(Matrix4f mat) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(4*4);
            mat.get(fb);
            glUniformMatrix4fv(location, false, fb);
        }
    }
}
