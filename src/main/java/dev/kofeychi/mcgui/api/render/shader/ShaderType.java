package dev.kofeychi.mcgui.api.render.shader;

import static org.lwjgl.opengl.GL20.*;

public record ShaderType(
        String name,
        String fileExtension,
        int glType
) {
    public static final ShaderType FRAGMENT = new ShaderType(
            "fragment",
            "fsh",
            GL_FRAGMENT_SHADER
    );
    public static final ShaderType VERTEX = new ShaderType(
            "vertex",
            "vsh",
            GL_VERTEX_SHADER
    );
}
