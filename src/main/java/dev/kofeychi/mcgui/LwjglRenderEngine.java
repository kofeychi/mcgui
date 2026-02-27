package dev.kofeychi.mcgui;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Главный класс приложения, демонстрирующий модульный рендер-движок.
 */
public class LwjglRenderEngine {

    // ==========================================
    // 1. Framerate Manager (Управление временем и FPS)
    // ==========================================
    public static class FramerateManager {
        private final int targetFps;
        private final double targetFrameTime;
        private double lastTime;
        private float deltaTime;

        public FramerateManager(int targetFps) {
            this.targetFps = targetFps;
            this.targetFrameTime = targetFps > 0 ? 1.0 / targetFps : 0.0;
            this.lastTime = glfwGetTime();
        }

        public void update() {
            double currentTime = glfwGetTime();
            deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;
        }

        public void sync() {
            if (targetFps <= 0) return;
            double syncTime = lastTime + targetFrameTime;
            while (glfwGetTime() < syncTime) {
                LockSupport.parkNanos(500_000L);
            }
        }

        public float getDeltaTime() { return deltaTime; }
    }

    // ==========================================
    // 2. Shader System (Управление шейдерами)
    // ==========================================
    public static class ShaderInstance {
        private final int programId;
        private final Map<String, Integer> uniformLocationCache = new HashMap<>();

        public ShaderInstance(String vertexSrc, String fragmentSrc) {
            int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSrc);
            int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSrc);

            programId = glCreateProgram();
            glAttachShader(programId, vertexShader);
            glAttachShader(programId, fragmentShader);
            glLinkProgram(programId);

            if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
                throw new RuntimeException("Shader linking failed: " + glGetProgramInfoLog(programId));
            }

            // Очищаем исходники шейдеров из памяти GPU
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
        }

        private int compileShader(int type, String source) {
            int shader = glCreateShader(type);
            glShaderSource(shader, source);
            glCompileShader(shader);
            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shader));
            }
            return shader;
        }

        public void bind() { glUseProgram(programId); }
        public void unbind() { glUseProgram(0); }
        public void cleanup() { glDeleteProgram(programId); }

        public int getUniformLocation(String uniformName) {
            return uniformLocationCache.computeIfAbsent(uniformName, k -> glGetUniformLocation(programId, k));
        }

        // Пример установки Uniform
        public void setUniform1f(String name, float value) {
            glUniform1f(getUniformLocation(name), value);
        }
    }

    public static class ShaderManager {
        private final Map<String, ShaderInstance> shaders = new HashMap<>();

        public void loadShader(String name, String vertexSource, String fragmentSource) {
            shaders.put(name, new ShaderInstance(vertexSource, fragmentSource));
        }

        public ShaderInstance getShader(String name) {
            return shaders.get(name);
        }

        public void cleanup() {
            for (ShaderInstance shader : shaders.values()) {
                shader.cleanup();
            }
            shaders.clear();
        }
    }

    // ==========================================
    // 3. Vertex Processing (Генерация геометрии)
    // ==========================================
    
    // Интерфейс в стиле Minecraft / Immediate Mode для удобства API
    public interface VertexConsumer {
        VertexConsumer pos(float x, float y, float z);
        VertexConsumer color(float r, float g, float b, float a);
        VertexConsumer uv(float u, float v);
        void endVertex();
    }

    // Реализация, пишущая напрямую в off-heap память (максимальная производительность)
    public static class BufferBuilder implements VertexConsumer {
        // Формат: POS(3) + COLOR(4) + UV(2) = 9 float'ов = 36 байт на вертекс
        public static final int VERTEX_STRIDE_BYTES = 36; 
        
        private ByteBuffer buffer;
        private int vertexCount;
        
        // Временное хранилище атрибутов до вызова endVertex()
        private float x, y, z;
        private float r = 1, g = 1, b = 1, a = 1;
        private float u = 0, v = 0;

        public BufferBuilder(int initialCapacityVertices) {
            buffer = memAlloc(initialCapacityVertices * VERTEX_STRIDE_BYTES);
        }

        public void begin() {
            buffer.clear();
            vertexCount = 0;
        }

        @Override
        public VertexConsumer pos(float x, float y, float z) {
            this.x = x; this.y = y; this.z = z; return this;
        }

        @Override
        public VertexConsumer color(float r, float g, float b, float a) {
            this.r = r; this.g = g; this.b = b; this.a = a; return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            this.u = u; this.v = v; return this;
        }

        @Override
        public void endVertex() {
            ensureCapacity(VERTEX_STRIDE_BYTES);
            buffer.putFloat(x).putFloat(y).putFloat(z);
            buffer.putFloat(r).putFloat(g).putFloat(b).putFloat(a);
            buffer.putFloat(u).putFloat(v);
            vertexCount++;
            
            // Сброс цвета и UV по умолчанию
            r = g = b = a = 1.0f; 
            u = v = 0.0f;
        }

        private void ensureCapacity(int neededBytes) {
            if (buffer.remaining() < neededBytes) {
                int newCapacity = buffer.capacity() * 2;
                ByteBuffer newBuffer = memAlloc(newCapacity);
                buffer.flip();
                newBuffer.put(buffer);
                memFree(buffer);
                buffer = newBuffer;
            }
        }

        // Финализирует буфер и выгружает в видеопамять
        public BuiltMesh upload() {
            buffer.flip();
            BuiltMesh mesh = new BuiltMesh();
            mesh.uploadData(buffer, vertexCount);
            return mesh;
        }

        public void cleanup() {
            if (buffer != null) memFree(buffer);
        }
    }

    // Кэшированная геометрия в видеопамяти (VBO/VAO)
    public static class BuiltMesh {
        private final int vao, vbo;
        private int vertexCount;

        public BuiltMesh() {
            vao = glGenVertexArrays();
            vbo = glGenBuffers();
        }

        public void uploadData(ByteBuffer data, int count) {
            this.vertexCount = count;
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

            int stride = BufferBuilder.VERTEX_STRIDE_BYTES;
            
            // Position (Location 0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
            glEnableVertexAttribArray(0);
            
            // Color (Location 1)
            glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, 3 * 4);
            glEnableVertexAttribArray(1);

            // UV (Location 2)
            glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, (3 + 4) * 4);
            glEnableVertexAttribArray(2);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        public void draw() {
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
            glBindVertexArray(0);
        }

        public void cleanup() {
            glDeleteBuffers(vbo);
            glDeleteVertexArrays(vao);
        }
    }

    // ==========================================
    // 4. Render System (Ядро рендеринга)
    // ==========================================
    
    // Объект контекста передается в метод рендера. Содержит параметры кадра.
    public static class RenderContext {
        public final float deltaTime;
        public final float totalTime;
        public final int windowWidth;
        public final int windowHeight;

        public RenderContext(float deltaTime, float totalTime, int width, int height) {
            this.deltaTime = deltaTime;
            this.totalTime = totalTime;
            this.windowWidth = width;
            this.windowHeight = height;
        }
    }

    public static class RenderSystem {
        private final ShaderManager shaderManager;
        private final BufferBuilder bufferBuilder;
        private BuiltMesh cachedMesh;

        public RenderSystem() {
            shaderManager = new ShaderManager();
            bufferBuilder = new BufferBuilder(1024); // Выделяем память под 1024 вершин
        }

        public void init() {
            // Базовые настройки OpenGL
            glEnable(GL_DEPTH_TEST);
            glClearColor(0.1f, 0.15f, 0.2f, 1.0f);

            // Загрузка кастомного шейдера
            String vertSrc = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                layout (location = 1) in vec4 aColor;
                layout (location = 2) in vec2 aUv;
                
                out vec4 vertexColor;
                uniform float uTime;
                
                void main() {
                    // Простая анимация на GPU с использованием uTime
                    float yOffset = sin(uTime * 3.0 + aPos.x * 5.0) * 0.1;
                    gl_Position = vec4(aPos.x, aPos.y + yOffset, aPos.z, 1.0);
                    vertexColor = aColor;
                }
                """;
            
            String fragSrc = """
                #version 330 core
                in vec4 vertexColor;
                out vec4 FragColor;
                
                void main() {
                    FragColor = vertexColor;
                }
                """;
                
            shaderManager.loadShader("main", vertSrc, fragSrc);

            // Собираем статичную геометрию ОДИН раз (кэширование а-ля Minecraft Chunks)
            bufferBuilder.begin();
            // Вершина 1 (Лево низ)
            bufferBuilder.pos(-0.5f, -0.5f, 0.0f).color(1f, 0f, 0f, 1f).uv(0f, 0f).endVertex();
            // Вершина 2 (Право низ)
            bufferBuilder.pos(0.5f, -0.5f, 0.0f).color(0f, 1f, 0f, 1f).uv(1f, 0f).endVertex();
            // Вершина 3 (Центр верх)
            bufferBuilder.pos(0.0f, 0.5f, 0.0f).color(0f, 0f, 1f, 1f).uv(0.5f, 1f).endVertex();
            
            // Выгружаем геометрию в GPU (VBO/VAO)
            cachedMesh = bufferBuilder.upload();
        }

        // Выделенный метод рендеринга со всеми параметрами
        public void render(RenderContext context) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glViewport(0, 0, context.windowWidth, context.windowHeight);

            ShaderInstance shader = shaderManager.getShader("main");
            shader.bind();
            
            // Передаем время для анимации в шейдере
            shader.setUniform1f("uTime", context.totalTime);

            // Отрисовываем закэшированную геометрию
            if (cachedMesh != null) {
                cachedMesh.draw();
            }

            shader.unbind();
        }

        public void cleanup() {
            if (cachedMesh != null) cachedMesh.cleanup();
            bufferBuilder.cleanup();
            shaderManager.cleanup();
        }
    }

    // ==========================================
    // Точка входа приложения
    // ==========================================
    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        
        // Для Mac OS
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        int width = 800;
        int height = 600;
        long window = glfwCreateWindow(width, height, "Modular Render Engine Demo", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0); // Отключаем VSync (чтобы работал наш FramerateManager)
        glfwShowWindow(window);

        // Критически важно для LWJGL! Подхватывает текущий контекст OpenGL.
        GL.createCapabilities();

        RenderSystem renderSystem = new RenderSystem();
        renderSystem.init();

        // Ограничиваем FPS до 60
        FramerateManager fpsManager = new FramerateManager(60);

        int[] w = new int[1];
        int[] h = new int[1];

        // Игровой / рендер луп
        while (!glfwWindowShouldClose(window)) {
            fpsManager.update();
            
            glfwGetFramebufferSize(window, w, h);
            
            // Создаем контекст кадра
            RenderContext ctx = new RenderContext(
                fpsManager.getDeltaTime(), 
                (float)glfwGetTime(), 
                w[0], 
                h[0]
            );

            // Отрисовка
            renderSystem.render(ctx);

            glfwSwapBuffers(window);
            glfwPollEvents();

            // Ожидание для ограничения FPS
            fpsManager.sync();
        }

        // Освобождение ресурсов
        renderSystem.cleanup();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}