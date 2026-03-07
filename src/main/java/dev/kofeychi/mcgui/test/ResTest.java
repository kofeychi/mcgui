package dev.kofeychi.mcgui.test;

import dev.kofeychi.mcgui.api.render.shader.Manager;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.shader.Shader;
import dev.kofeychi.mcgui.api.render.shader.ShaderType;
import dev.kofeychi.mcgui.todo.render.FpsController;
import dev.kofeychi.mcgui.todo.render.buf.BufferedBuilder;
import dev.kofeychi.mcgui.todo.render.buf.BuiltBuffer;
import dev.kofeychi.mcgui.todo.render.buf.format.VertexFormat;
import dev.kofeychi.mcgui.todo.render.shader.ShaderManager;
import dev.kofeychi.mcgui.todo.resource.Id;
import dev.kofeychi.mcgui.todo.resource.PackType;
import dev.kofeychi.mcgui.todo.resource.types.InjarPackSource;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ResTest {
    public static void main(String[] args) throws Exception {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        long window = glfwCreateWindow(800, 800, "alo", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Инициализация "Unsafe" строителя буферов (без ByteBuffer)
        BufferedBuilder builder = new BufferedBuilder(2048,VertexFormat.POSITION_COLOR_TEX,GL_TRIANGLES);
        var ma = new Matrix4f();
        ma.ortho(0, 800,800, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        ma.scale(4);
        builder.vertex(ma,0,0, 1.0f).color(1,0,0,1).texture(0,0).push();
        builder.vertex(ma,0,50, 1.0f).color(0,1,0,1).texture(1,0).push();
        builder.vertex(ma,50,0, 1.0f).color(0,0,1,1).texture(.5f,1).push();
        builder.vertex(ma, 50,0, -2.0f).color(1,0,0,1).texture(0,0).push();
        builder.vertex(ma, 50,50, -2.0f).color(0,1,0,1).texture(1,0).push();
        builder.vertex(ma,50+50,0, -2.0f).color(0,0,1,1).texture(.5f,1).push();
        BuiltBuffer mesh = builder.build();

        var shm = Manager.empty();
        var ref = shm.load(
                "test",
                Program.from(List.of(
                        Shader.from(
                                ShaderType.VERTEX,
                                read(ResTest.class.getResourceAsStream("/pack/client/builtin/shader/core/test.vsh"))
                        ),
                        Shader.from(
                                ShaderType.FRAGMENT,
                                read(ResTest.class.getResourceAsStream("/pack/client/builtin/shader/core/test.fsh"))
                        )
                ))
        );

        FpsController time = new FpsController(()->75);

        var m = mesh.toMesh();
        while (!glfwWindowShouldClose(window)) {
            time.update();
            ref.bind();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            ref.getUniforms("uTime").setFloat((float) glfwGetTime());
            m.draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
            ref.unbind();
            time.sync();
        }

        m.cleanup();
        builder.close();
        shm.close();
        glfwTerminate();
    }
    public static String read(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
