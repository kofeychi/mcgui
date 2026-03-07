package dev.kofeychi.mcgui.test;

import dev.kofeychi.mcgui.api.render.FpsController;
import dev.kofeychi.mcgui.api.render.shader.Manager;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.shader.Shader;
import dev.kofeychi.mcgui.api.render.shader.ShaderType;
import dev.kofeychi.mcgui.api.render.vertex.Builder;
import dev.kofeychi.mcgui.api.render.vertex.format.Formats;
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

        var ma = new Matrix4f();
        ma.ortho(0, 800,800, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        ma.scale(4);



        var b = Builder.from(1024,Formats.POS_COLOR_TEX);
        b.vertex(ma,0,0,0).color(1,0,0,1).texture(0,0).push();
        b.vertex(ma,50,0,0).color(0,1,0,1).texture(1,0).push();
        b.vertex(ma,0,50,0).color(0,0,1,1).texture(.5f,1).push();
        var built = b.build();
        var mesh = built.toMesh();
        mesh.upload();

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
                ), Formats.POS_COLOR_TEX)
        );



        FpsController time = new FpsController(()->75);

        while (!glfwWindowShouldClose(window)) {
            time.update();
            ref.bind();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            ref.getUniforms("uTime").setFloat((float) glfwGetTime());
            mesh.draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
            ref.unbind();
            time.sync();
        }

        mesh.close();
        b.close();
        shm.close();
        glfwTerminate();
    }
    public static String read(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
