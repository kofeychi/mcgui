package dev.kofeychi.mcgui.test;

import dev.kofeychi.mcgui.api.render.Context;
import dev.kofeychi.mcgui.api.render.Helpers;
import dev.kofeychi.mcgui.api.render.app.Application;
import dev.kofeychi.mcgui.api.render.app.Render;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.shader.Reference;
import dev.kofeychi.mcgui.api.render.shader.Shader;
import dev.kofeychi.mcgui.api.render.shader.ShaderType;
import dev.kofeychi.mcgui.api.render.vertex.format.Formats;
import dev.kofeychi.mcgui.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class BasicQuadApplication {
    public Reference pos_col;
    public Matrix4f projection = new Matrix4f();
    public Context ctx;

    public static void main(String[] args) throws Exception {
        new BasicQuadApplication().run(args);
    }

    public void run(String[] args) throws Exception {
        var app = Application.basic();
        var r = app.getRender();
        var w = r.getWindow();
        r.preInit().add(this::preInit);
        r.update().add(this::render);
        r.setupRender().add(this::preRender);
        w.hint(GLFW_TRANSPARENT_FRAMEBUFFER,GLFW_TRUE);
        w.framebufferSize().add((x,y)->{
            System.out.println(new Vector2i(x,y).toString(NumberFormat.getIntegerInstance()));
            projection = Helpers.screen(
                    x,
                    y,
                    4,
                    -500,
                    500
            );
            glViewport(0,0,x,y);
        });
        w.hint(GLFW_DECORATED,0);
        app.init();
        glfwSetWindowSize(w.getHandle(),250,250);
        glfwSetWindowPos(w.getHandle(),1920/2-(250/2),1080/2-(130/2));
        app.startLoop();
        app.close();
    }

    public void render(Application app) {
        ctx.fill(0,0,50,50,0, Color.ofRGB(10,10,10));
        ctx.fill(1,1,49,49,1, Color.ofRGB(48,48,48));
        ctx.fill(2,2,48,48,2, Color.ofRGB(34,34,34));
        for (int i = 0; i < 5; i++) {
            ctx.getMatrices().push();
            var mat = new Matrix4f();
            mat.translation(25,25,0);
            mat.rotate((float) (glfwGetTime()+Math.toRadians(i*20)),0,0,1);
            mat.scale(.25f);

            ctx.getMatrices().peek().getPositionMatrix().set(mat);
            ctx.getMatrices().peek().getPositionMatrix().translate(-50,0,0);
            ctx.fill(-12,-12,12,12,3+i,Color.ofRGB(50,50,50).brighter(1+((double) (i+1) /10)));
            ctx.getMatrices().peek().getPositionMatrix().translate(100,0,0);
            ctx.fill(-12,-12,12,12,3+i,Color.ofRGB(50,50,50).brighter(1+((double) (i+1) /10)));
            ctx.getMatrices().pop();
        }
    }

    public void preRender(Render render) {
        pos_col.getUniforms("transformation").setFloatMat4(
                projection
        );
    }

    public void preInit(Application app) {
        try {
            preInitUnsafe(app);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void preInitUnsafe(Application app) throws Exception {
        pos_col = app.getRender().getShaderManager().load(
                "col",
                Program.from(List.of(
                        Shader.from(
                                ShaderType.VERTEX,
                                read(BasicQuadApplication.class.getResourceAsStream("/shader/core/position_color.vsh"))
                        ),
                        Shader.from(
                                ShaderType.FRAGMENT,
                                read(BasicQuadApplication.class.getResourceAsStream("/shader/core/position_color.fsh"))
                        )
                ), Formats.POS_COLOR)
        );
        pos_col.bind();
        ctx = Context.from(app.getRender().getSharedSource(), pos_col.getProgram());
    }
    public static String read(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
