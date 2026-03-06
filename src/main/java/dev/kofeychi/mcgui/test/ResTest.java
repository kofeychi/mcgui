package dev.kofeychi.mcgui.test;

import dev.kofeychi.mcgui.todo.render.FpsController;
import dev.kofeychi.mcgui.todo.render.buf.BufferedBuilder;
import dev.kofeychi.mcgui.todo.render.buf.BuiltBuffer;
import dev.kofeychi.mcgui.todo.render.buf.format.VertexFormat;
import dev.kofeychi.mcgui.todo.render.shader.ShaderManager;
import dev.kofeychi.mcgui.todo.resource.Id;
import dev.kofeychi.mcgui.todo.resource.PackType;
import dev.kofeychi.mcgui.todo.resource.types.InjarPackSource;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ResTest {
    public static PackType shader = new PackType(
            "client/builtin/shader",
            Id.of("builtin","client")
    );
    public static void main(String[] args) throws Exception {
        var ps = new InjarPackSource(
                ResTest.class,
                Id.of("builtin","test"),
                shader
        );
        var vertexsh = ps.resolve("core/test.vsh");
        var fragsh = ps.resolve("core/test.fsh");
        System.out.println(vertexsh.target());
        System.out.println(fragsh.target());
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        long window = glfwCreateWindow(800, 600, "alo", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Инициализация "Unsafe" строителя буферов (без ByteBuffer)
        BufferedBuilder builder = new BufferedBuilder(2048,VertexFormat.POSITION_COLOR_TEX,GL_TRIANGLES);
        builder.vertex(-0.5f, -0.5f, 1.0f).color(1,0,0,1).texture(0,0).push();
        builder.vertex( 0.5f, -0.5f, 1.0f).color(0,1,0,1).texture(1,0).push();
        builder.vertex( 0.0f,  0.5f, 1.0f).color(0,0,1,1).texture(.5f,1).push();
        builder.vertex(-0.5f+.5f, -0.5f, 0.0f).color(1,0,0,1).texture(0,0).push();
        builder.vertex( 0.5f+.5f, -0.5f, 0.0f).color(0,1,0,1).texture(1,0).push();
        builder.vertex( 0.0f+.5f,  0.5f, 0.0f).color(0,0,1,1).texture(.5f,1).push();
        BuiltBuffer mesh = builder.build();

        ShaderManager shaderManager = new ShaderManager();
        var sh = shaderManager.load(
                "test",
                vertexsh.readString(),
                fragsh.readString()
        );

        FpsController time = new FpsController(()->75);

        var m = mesh.toMesh();
        while (!glfwWindowShouldClose(window)) {
            time.update();
            sh.bind();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_BLEND);
            sh.setUniformf1("uTime", (float)glfwGetTime());
            m.draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
            sh.unbind();
            time.sync();
        }

        m.cleanup();
        builder.close();
        shaderManager.clean();
        glfwTerminate();
    }
}
