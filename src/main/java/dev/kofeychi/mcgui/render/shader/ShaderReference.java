package dev.kofeychi.mcgui.render.shader;

public class ShaderReference {
    public final ShaderManager manager;
    public final String name;

    public ShaderReference(ShaderManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    public void bind() {
        manager.getShader(name).bind();
    }

    public void unbind() {
        manager.getShader(name).unbind();
    }
}
