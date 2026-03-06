package dev.kofeychi.mcgui.todo.render.shader;

import static org.lwjgl.opengl.GL20.*;

public enum ShaderType {
    VERTEX("vsh", GL_VERTEX_SHADER),
    FRAGMENT("fsh", GL_FRAGMENT_SHADER);
    public final String fileExtension;
    public final int gltype;

    ShaderType(String fileExtension, int type) {
        this.fileExtension = fileExtension;
        this.gltype = type;
    }
}
