package dev.kofeychi.mcgui.todo.render.shader;

public class ShaderReference {
    public final ShaderManager manager;
    public final String name;
    public boolean valid = true;

    public ShaderReference(ShaderManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    public void bind() {
        if(!valid) {
            throw new RuntimeException("Invalid shader reference");
        }
        manager.getShader(name).bind();
    }

    public void unbind() {
        if(!valid) {
            throw new RuntimeException("Invalid shader reference");
        }
        manager.getShader(name).unbind();
    }

    public void setUniformf1(String uniform,float value) {
        if(!valid) {
            throw new RuntimeException("Invalid shader reference");
        }
        manager.getShader(name).setUniformf1(uniform, value);
    }

    public void clean() {
        valid = false;
        manager.getShader(name).clean();
    }
}
