package dev.kofeychi.mcgui.test;

import dev.kofeychi.mcgui.api.render.Context;
import dev.kofeychi.mcgui.api.render.Helpers;
import dev.kofeychi.mcgui.api.render.app.Application;
import dev.kofeychi.mcgui.api.render.app.Render;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.shader.Reference;
import dev.kofeychi.mcgui.api.render.shader.Shader;
import dev.kofeychi.mcgui.api.render.shader.ShaderType;
import dev.kofeychi.mcgui.api.render.texture.TextureReference;
import dev.kofeychi.mcgui.api.render.vertex.format.Formats;
import dev.kofeychi.mcgui.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class BasicQuadApplication {
    public record Quad<RC1,RC2,RC3,RC4>(RC1 r1, RC2 r2, RC3 r3, RC4 r4) {}
    public record Duo<RC1,RC2>(RC1 r1, RC2 r2) {}

    public static class Area {
        public Consumer<Duo<Quad<Vector2i,Integer,Integer,Integer>,Application>> action;
        public Vector4i dim;
        public Quad<Color,Color,Color,Color> colors;

        public Area(Consumer<Duo<Quad<Vector2i,Integer,Integer,Integer>,Application>> action, Vector4i dim, Quad<Color,Color,Color,Color> colors) {
            this.action = action;
            this.dim = dim;
            this.colors = colors;
        }

        public void press(Quad<Vector2i,Integer,Integer,Integer> data,Application application) {
            if(over(data.r1)) {
                action.accept(new Duo<>(data, application));
            }
        }
        public boolean over(Vector2i pos) {
            return (
                    dim.x <= pos.x && dim.x+dim.z >= pos.x
            )&&(
                    dim.y <= pos.y && dim.y+dim.w >= pos.y
            );
        }
        public void render(Context context) {
            border(
                    context,
                    dim.x,
                    dim.y,
                    dim.x+dim.z,
                    dim.y+dim.w,
                    5,
                    colors.r1,
                    colors.r2,
                    colors.r3,
                    colors.r4
            );
        }
        public void border(Context ctx,int x1, int y1, int x2, int y2,int z,Color c1,Color c2,Color c3,Color c4) {
            ctx.fill(x1,y1,x2,y2+2,z, c1);
            ctx.fill(x1+1,y1+1,x2-1,y2-1,z+1, c2);
            ctx.fill(x1+2,y1+2,x2-2,y2-2,z+2, c3);
            ctx.fill(x1+1,y2-1,x2-1,y2+1,z+3, c4);
        }
    }
    public static Application application;
    public Reference pos_tex;
    public Reference pos_col;
    public Matrix4f projection = new Matrix4f();
    public Context ctx;
    public static final int scale = 2;

    public Area close = new Area(
            app -> {
                glfwSetWindowShouldClose(app.r2.getRender().getWindow().getHandle(), true);
            },
            new Vector4i(),
            new Quad<>(
                    Color.ofRGB(34, 3, 5),
                    Color.ofRGB(241, 75, 47),
                    Color.ofRGB(102, 11, 17),
                    Color.ofRGB(68, 7, 11)
            )
    );
    public Area max = new Area(
            app -> {
                toggleMaximize(app.r2.getRender().getWindow().getHandle());
            },
            new Vector4i(),
            new Quad<>(
                    Color.ofRGB(2, 34, 22),
                    Color.ofRGB(228, 255, 120),
                    Color.ofRGB(11, 82, 38),
                    Color.ofRGB(11, 55, 28)
            )
    );
    public Area min = new Area(
            app -> {
                glfwIconifyWindow(app.r2.getRender().getWindow().getHandle());
            },
            new Vector4i(),
            new Quad<>(
                    Color.ofRGB(34, 3, 5),
                    Color.ofRGB(108, 196, 241),
                    Color.ofRGB(29, 59, 88),
                    Color.ofRGB(23, 44, 66)
            )
    );
    public boolean resizing = false;
    public Area resize = new Area(
            app -> {
                resizing = app.r1.r3 == GLFW_PRESS;
            },
            new Vector4i(),
            new Quad<>(
                    Color.ofRGB(34, 3, 5),
                    Color.ofRGB(108, 196, 241),
                    Color.ofRGB(29, 59, 88),
                    Color.ofRGB(23, 44, 66)
            )
    );
    public boolean dragging = false;
    public Vector2i dragOffset = new Vector2i();
    public Area drag = new Area(
            app -> {
                dragging = app.r1.r3 == GLFW_PRESS;
                dragOffset = app.r2.getRender().getMouse().glfwget().get(2,new Vector2i());
            },
            new Vector4i(),
            new Quad<>(
                    Color.ofRGB(10,10,10),
                    Color.ofRGB(48,48,48),
                    Color.ofRGB(34,34,34),
                    Color.ofRGB(33,33,33)
            )
    );

    private void toggleMaximize(long window) {
        if (glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == GLFW_TRUE) {
            glfwRestoreWindow(window);
        } else {
            glfwMaximizeWindow(window);
        }
    }

    public static void main(String[] args) throws Exception {
        new BasicQuadApplication().run(args);
    }

    public void run(String[] args) throws Exception {
        var app = Application.basic();
        application = app;
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
                    scale,
                    -100,
                    100
            );
            glViewport(0,0,x,y);
            rebuild(new Vector2i(x,y).div(scale));
        });
        r.getMouse().button().add((button,action,mods)->{
            var mpos = mousePos();
            var q = new Quad<>(mpos,button,action,mods);
            close.press(q,app);
            max.press(q,app);
            min.press(q,app);
            resize.press(q,app);
            drag.press(q,app);
            rebuild(new Vector2i(w.getFramebufferWidth(),w.getFramebufferHeight()).div(scale));
        });
        r.getMouse().position().add((x,y)->{
            if(resizing) {
                // Так как точка якоря (верхний левый угол) остается на месте при ресайзе
                // из нижнего правого угла, xpos и ypos напрямую являются нашей новой шириной и высотой!
                int newWidth = (int) Math.max(64*scale, x);
                int newHeight = (int) Math.max(64*scale, y);
                glfwSetWindowSize(w.getHandle(), newWidth, newHeight);
            }
            if(dragging) {
                if (glfwGetWindowAttrib(w.getHandle(), GLFW_MAXIMIZED) == GLFW_TRUE) {
                    return;
                }
                try (MemoryStack stack = stackPush()) {
                    IntBuffer pX = stack.mallocInt(1);
                    IntBuffer pY = stack.mallocInt(1);
                    glfwGetWindowPos(w.getHandle(), pX, pY);

                    // Вычисляем новую позицию окна в абсолютных координатах экрана
                    int screenX = pX.get(0) + (int)x;
                    int screenY = pY.get(0) + (int)y;

                    glfwSetWindowPos(w.getHandle(), screenX - (int)dragOffset.x, screenY - (int)dragOffset.y);
                }
            }
        });
        w.hint(GLFW_DECORATED,0);
        app.init();
        var ww = 64*scale;
        var wh = (66+17)*scale;
        glfwSetWindowSize(w.getHandle(),ww,wh);
        glfwSetWindowPos(w.getHandle(),1920/2-(ww/2),1080/2-(wh/2));
        app.startLoop();
        app.close();
    }

    public static Vector2i mousePos() {
        return new Vector2i(
                (int) (application.getRender().getMouse().getX()/scale),
                (int) (application.getRender().getMouse().getY()/scale)
        );
    }

    public void border(int x1, int y1, int x2, int y2,int z) {
        ctx.fill(x1,y1,x2,y2+2,z, Color.ofRGB(10,10,10));
        ctx.fill(x1+1,y1+1,x2-1,y2-1,z+1, Color.ofRGB(48,48,48));
        ctx.fill(x1+2,y1+2,x2-2,y2-2,z+2, Color.ofRGB(34,34,34));
        ctx.fill(x1+1,y2-1,x2-1,y2+1,z, Color.ofRGB(33,33,33));
    }

    public void render(Application app) {
        ctx.draw();
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
    public void rebuild(Vector2i size) {
        ctx.invalidateCache();
        AtomicInteger a = new AtomicInteger();
        Runnable add = () -> a.addAndGet(17);

        close.dim = new Vector4i(
                (size.x)-14,0,
                12,12
        );
        max.dim = new Vector4i(
                (size.x)-14-1-14,0,
                12,12
        );
        min.dim = new Vector4i(
                (size.x)-14-1-14-1-14,0,
                12,12
        );
        resize.dim = new Vector4i(
                (size.x)-5,size.y-5,5,5
        );
        drag.dim = new Vector4i(
                0,0, (int) (size.x)-48,16
        );
        border((int) (size.x)-49,0, (int) (size.x),16,0); // buttons
        add.run();
        border(0, a.get(),size.x,size.y-2,0);
        close.render(ctx);
        max.render(ctx);
        min.render(ctx);
        resize.render(ctx);
        drag.render(ctx);
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
        pos_tex = app.getRender().getShaderManager().load(
                "tex",
                Program.from(List.of(
                        Shader.from(
                                ShaderType.VERTEX,
                                read(BasicQuadApplication.class.getResourceAsStream("/shader/core/position_texture.vsh"))
                        ),
                        Shader.from(
                                ShaderType.FRAGMENT,
                                read(BasicQuadApplication.class.getResourceAsStream("/shader/core/position_texture.fsh"))
                        )
                ), Formats.POS_TEX)
        );
        pos_col.bind();
        ctx = Context.cached(app.getRender().getSharedSource(), pos_col.getProgram());
        rebuild(new Vector2i(80,80));
    }
    public static String read(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
