package dev.kofeychi.mcgui.impl.render.app;

import dev.kofeychi.mcgui.api.Event;
import dev.kofeychi.mcgui.api.render.FpsController;
import dev.kofeychi.mcgui.api.render.app.Application;
import dev.kofeychi.mcgui.api.render.app.Mouse;
import dev.kofeychi.mcgui.api.render.app.Render;
import dev.kofeychi.mcgui.api.render.app.Window;
import dev.kofeychi.mcgui.api.render.shader.Manager;
import dev.kofeychi.mcgui.api.render.texture.TextureManager;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.util.function.DoubleSupplier;
import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RenderImpl implements Render {
    private final TextureManager textureManager;
    private final Application host;
    private final Window window;
    private final Mouse mouse;
    private final Manager manager;
    private final BuilderSource builder;
    private DoubleSupplier supplier = ()->75;
    private final FpsController fps = new FpsController(supplier);
    private boolean init = false;

    private final Event<Update> updateEvent = Event.create(
            Update.class,
            (arr) -> (h) -> {
                for(var u : arr) {
                    u.on(h);
                }
            }
    );

    private final Event<PreInit> preEvent = Event.create(
            PreInit.class,
            (arr) -> (h) -> {
                for(var u : arr) {
                    u.on(h);
                }
            }
    );
    private final Event<PostInit> postEvent = Event.create(
            PostInit.class,
            (arr) -> (h) -> {
                for(var u : arr) {
                    u.on(h);
                }
            }
    );

    private final Event<SetupRender> setupRenderEvent = Event.create(
            SetupRender.class,
            (arr) -> (h) -> {
                for(var u : arr) {
                    u.setup(h);
                }
            }
    );

    private final Event<PostRender> postRenderEvent = Event.create(
            PostRender.class,
            (arr) -> (h) -> {
                for(var u : arr) {
                    u.post(h);
                }
            }
    );

    private final Event<Close> onClose = Event.create(
            Close.class,
            (arr) -> (h) -> {
                for(var u : arr) {
                    u.close(h);
                }
            }
    );

    public RenderImpl(Application host) {
        this.host = host;
        this.textureManager = TextureManager.empty();
        this.window = Window.empty(host);
        this.mouse = Mouse.create(this);
        this.manager = Manager.empty();
        this.builder = BuilderSource.create();
    }

    @Override
    public void initLoop() {
        if(!init) {
            init();
        }
        postEvent.invoker().on(host);
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var x  = stack.mallocInt(1);
            var y = stack.mallocInt(1);
            glfwGetFramebufferSize(window.getHandle(),x,y);
            window.framebufferSize().invoker().on(x.get(0), y.get(0));
        }
        while(!(glfwWindowShouldClose(window.getHandle()))) {
            fps.update();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            setupRenderEvent.invoker().setup(this);
            updateEvent.invoker().on(host);
            postRenderEvent.invoker().post(this);
            glfwSwapBuffers(window.getHandle());
            glfwPollEvents();
            fps.sync();
        }
        onClose.invoker().close(this);
        close();
    }

    @Override
    public BuilderSource getSharedSource() {
        return builder;
    }

    @Override
    public Manager getShaderManager() {
        return manager;
    }

    @Override
    public TextureManager getTextureManager() {
        return textureManager;
    }

    @Override
    public FpsController getFpsController() {
        return fps;
    }

    @Override
    public Application getHost() {
        return host;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public Mouse getMouse() {
        return mouse;
    }

    @Override
    public Event<SetupRender> setupRender() {
        return setupRenderEvent;
    }

    @Override
    public Event<PostRender> postRender() {
        return postRenderEvent;
    }

    @Override
    public Event<Close> onClose() {
        return onClose;
    }

    @Override
    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        for(int key : window.getHints().keySet()) {
            glfwWindowHint(key, window.getHints().get(key));
        }

        long window = glfwCreateWindow(800, 800, "Render", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        preEvent.invoker().on(host);
        this.window.init(window);
        this.mouse.init(window);
        init = true;
    }

    @Override
    public void setTargetUps(Function<Application, DoubleSupplier> targetUps) {
        supplier = targetUps.apply(host);
    }

    @Override
    public Event<Update> update() {
        return updateEvent;
    }

    @Override
    public Event<PostInit> postInit() {
        return postEvent;
    }

    @Override
    public Event<PreInit> preInit() {
        return preEvent;
    }

    @Override
    public void close() {
        this.mouse.close();
        this.window.close();
        this.manager.close();
        this.builder.close();
        glfwTerminate();
    }
}
