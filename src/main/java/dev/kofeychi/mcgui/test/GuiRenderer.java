package dev.kofeychi.mcgui.test;

import org.lwjgl.opengl.GL;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Профессиональная реализация GUI с поддержкой Scaled Resolution (как в Minecraft).
 * 1. Обработка изменения размера фреймбуфера.
 * 2. Расчет логических координат GUI для сохранения аспектного соотношения.
 * 3. Синхронизация координат мыши с логической сеткой.
 */
public class GuiRenderer {

    private long window;
    
    // Физические размеры окна (в пикселях)
    private int fbWidth = 1280;
    private int fbHeight = 720;

    // Логические размеры GUI (Scaled Resolution)
    private float guiWidth;
    private float guiHeight;
    private int guiScale = 2; // Коэффициент масштабирования (как в настройках Minecraft)

    private Matrix4f projectionMatrix;
    private Matrix4f modelMatrix;
    private Vector2f mousePos;

    public void run() {
        init();
        loop();
        glfwTerminate();
    }

    private void init() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(fbWidth, fbHeight, "Pro GUI Scaled Resolution", 0, 0);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        mousePos = new Vector2f();

        // Установка Callback для изменения размера окна
        glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            this.fbWidth = w;
            this.fbHeight = h;
            glViewport(0, 0, w, h);
            updateScaledResolution();
        });

        // Первичный расчет
        updateScaledResolution();
    }

    /**
     * Ключевой метод: рассчитывает логические размеры GUI.
     * Мы делим физические пиксели на масштаб. 
     * В итоге 1 единица в OpenGL = guiScale пикселей.
     */
    private void updateScaledResolution() {
        // Рассчитываем, сколько логических единиц помещается в текущее окно
        this.guiWidth = (float) fbWidth / guiScale;
        this.guiHeight = (float) fbHeight / guiScale;

        // Обновляем ортографическую проекцию, чтобы 0,0 был в Top-Left
        // а правый нижний угол был guiWidth, guiHeight
        projectionMatrix.identity().ortho(0, guiWidth, guiHeight, 0, -1, 1);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            updateMousePosition();

            // Рисуем квадрат 50x50 в логических единицах
            // При масштабе 2 он будет занимать 100x100 физических пикселей
            renderRectAtMouse(50, 50);

            // Пример статичного элемента в углу
            renderStaticRect(guiWidth - 60, 10, 50, 50);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void updateMousePosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.mallocDouble(1);
            DoubleBuffer y = stack.mallocDouble(1);
            glfwGetCursorPos(window, x, y);
            
            // Превращаем экранные координаты в логические координаты GUI
            float scaledX = (float) x.get(0) / guiScale;
            float scaledY = (float) y.get(0) / guiScale;
            
            mousePos.set(scaledX, scaledY);
        }
    }

    private void renderRectAtMouse(float w, float h) {
        modelMatrix.identity().translate(mousePos.x, mousePos.y, 0);
        applyMatricesAndDraw(w, h, 0.2f, 0.6f, 1.0f);
    }

    private void renderStaticRect(float x, float y, float w, float h) {
        modelMatrix.identity().translate(x, y, 0);
        applyMatricesAndDraw(w, h, 0.8f, 0.2f, 0.2f);
    }

    private void applyMatricesAndDraw(float w, float h, float r, float g, float b) {
        glMatrixMode(GL_PROJECTION);
        float[] projBuf = new float[16];
        projectionMatrix.get(projBuf);
        glLoadMatrixf(projBuf);

        glMatrixMode(GL_MODELVIEW);
        float[] modelBuf = new float[16];
        modelMatrix.get(modelBuf);
        glLoadMatrixf(modelBuf);

        glBegin(GL_QUADS);
        glColor3f(r, g, b);
        glVertex2f(0, 0);
        glVertex2f(w, 0);
        glVertex2f(w, h);
        glVertex2f(0, h);
        glEnd();
    }

    public static void main(String[] args) {
        new GuiRenderer().run();
    }
}