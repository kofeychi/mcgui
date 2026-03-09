package dev.kofeychi.mcgui.test;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Пример приложения на LWJGL 3 с кастомным заголовком окна (Borderless Window).
 * Реализовано: перемещение за заголовок, кнопки Закрыть/Развернуть/Свернуть,
 * двойной клик для максимизации.
 */
public class CustomTitleBarApp {

    // Размеры и константы UI
    private int windowWidth = 800;
    private int windowHeight = 600;
    private final int TITLE_BAR_HEIGHT = 30;
    private final int BUTTON_WIDTH = 45;
    private final int RESIZE_HANDLE_SIZE = 15;
    private final int MIN_WIDTH = 400;
    private final int MIN_HEIGHT = 300;

    // Состояния интерфейса
    private enum HoverState { NONE, TITLE_BAR, MINIMIZE, MAXIMIZE, CLOSE, RESIZE_HANDLE }
    private HoverState currentHover = HoverState.NONE;
    private boolean isDragging = false;
    private boolean isResizing = false;

    // Переменные для логики перемещения окна
    private double dragOffsetX, dragOffsetY;
    private double lastClickTime = 0.0;

    // Дескриптор окна GLFW
    private long window;
    private long arrowCursor;
    private long resizeCursor;

    public void run() {
        init();
        loop();

        // Очистка ресурсов при завершении
        glfwDestroyCursor(arrowCursor);
        glfwDestroyCursor(resizeCursor);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Настройка обработчика ошибок
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Не удалось инициализировать GLFW");
        }

        // Конфигурация окна
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // ВАЖНО: Отключаем стандартные рамки ОС
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        window = glfwCreateWindow(windowWidth, windowHeight, "Custom UI", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Не удалось создать окно GLFW");
        }

        // Создание системных курсоров для визуального отклика
        arrowCursor = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        resizeCursor = glfwCreateStandardCursor(GLFW_RESIZE_NWSE_CURSOR);

        // Центрирование окна на экране
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        setupCallbacks();

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // V-Sync
        glfwShowWindow(window);
    }

    private void setupCallbacks() {
        // Обновление размеров области вывода
        glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
            windowWidth = w;
            windowHeight = h;
            glViewport(0, 0, w, h);
            setupOrthographicProjection();
        });

        // Отслеживание перемещения мыши
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            updateHoverState(xpos, ypos);

            // Логика перетаскивания окна
            if (isDragging) {
                // Запрещаем перемещение, если окно развернуто на весь экран
                if (glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == GLFW_TRUE) {
                    return;
                }

                try (MemoryStack stack = stackPush()) {
                    IntBuffer pX = stack.mallocInt(1);
                    IntBuffer pY = stack.mallocInt(1);
                    glfwGetWindowPos(window, pX, pY);

                    // Вычисляем новую позицию окна в абсолютных координатах экрана
                    int screenX = pX.get(0) + (int)xpos;
                    int screenY = pY.get(0) + (int)ypos;

                    glfwSetWindowPos(window, screenX - (int)dragOffsetX, screenY - (int)dragOffsetY);
                }
            } else if (isResizing) {
                // Так как точка якоря (верхний левый угол) остается на месте при ресайзе
                // из нижнего правого угла, xpos и ypos напрямую являются нашей новой шириной и высотой!
                int newWidth = Math.max(MIN_WIDTH, (int)xpos);
                int newHeight = Math.max(MIN_HEIGHT, (int)ypos);
                glfwSetWindowSize(window, newWidth, newHeight);
            }
        });

        // Обработка кликов
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    handleMousePress();
                } else if (action == GLFW_RELEASE) {
                    isDragging = false;
                    isResizing = false;

                    // Обновляем состояние курсора сразу после отпускания кнопки
                    try (MemoryStack stack = stackPush()) {
                        DoubleBuffer x = stack.mallocDouble(1);
                        DoubleBuffer y = stack.mallocDouble(1);
                        glfwGetCursorPos(window, x, y);
                        updateHoverState(x.get(0), y.get(0));
                    }
                }
            }
        });
    }

    private void updateHoverState(double x, double y) {
        if (isDragging || isResizing) return; // Не меняем hover во время активных действий

        boolean isHoveringResize = (x >= windowWidth - RESIZE_HANDLE_SIZE && y >= windowHeight - RESIZE_HANDLE_SIZE);

        if (isHoveringResize && glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == GLFW_FALSE) {
            currentHover = HoverState.RESIZE_HANDLE;
            glfwSetCursor(window, resizeCursor);
        } else if (y <= TITLE_BAR_HEIGHT) {
            glfwSetCursor(window, arrowCursor); // Возвращаем обычный курсор
            if (x >= windowWidth - BUTTON_WIDTH) {
                currentHover = HoverState.CLOSE;
            } else if (x >= windowWidth - BUTTON_WIDTH * 2) {
                currentHover = HoverState.MAXIMIZE;
            } else if (x >= windowWidth - BUTTON_WIDTH * 3) {
                currentHover = HoverState.MINIMIZE;
            } else {
                currentHover = HoverState.TITLE_BAR;
            }
        } else {
            currentHover = HoverState.NONE;
            glfwSetCursor(window, arrowCursor);
        }
    }

    private void handleMousePress() {
        try (MemoryStack stack = stackPush()) {
            DoubleBuffer xBuf = stack.mallocDouble(1);
            DoubleBuffer yBuf = stack.mallocDouble(1);
            glfwGetCursorPos(window, xBuf, yBuf);
            double mouseX = xBuf.get(0);
            double mouseY = yBuf.get(0);

            switch (currentHover) {
                case CLOSE:
                    glfwSetWindowShouldClose(window, true);
                    break;
                case MAXIMIZE:
                    toggleMaximize();
                    break;
                case MINIMIZE:
                    glfwIconifyWindow(window);
                    break;
                case RESIZE_HANDLE:
                    isResizing = true;
                    break;
                case TITLE_BAR:
                    // Проверка на двойной клик (порог 0.3 секунды)
                    double currentTime = glfwGetTime();
                    if (currentTime - lastClickTime < 0.3) {
                        toggleMaximize();
                    } else {
                        isDragging = true;
                        dragOffsetX = mouseX;
                        dragOffsetY = mouseY;
                    }
                    lastClickTime = currentTime;
                    break;
                default:
                    break;
            }
        }
    }

    private void toggleMaximize() {
        if (glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == GLFW_TRUE) {
            glfwRestoreWindow(window);
        } else {
            glfwMaximizeWindow(window);
        }
    }

    private void setupOrthographicProjection() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // Устанавливаем систему координат: (0,0) - верхний левый угол
        glOrtho(0, windowWidth, windowHeight, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
    }

    private void loop() {
        GL.createCapabilities();
        setupOrthographicProjection();

        // Включаем сглаживание линий для иконок
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        while (!glfwWindowShouldClose(window)) {
            // Очистка экрана (цвет фона основного окна: темно-серый #1E1E1E)
            glClearColor(0.12f, 0.12f, 0.12f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            renderUI();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void renderUI() {
        // 1. Отрисовка фона заголовка (#2D2D30)
        glColor3f(0.18f, 0.18f, 0.19f);
        drawRect(0, 0, windowWidth, TITLE_BAR_HEIGHT);

        // 2. Отрисовка кнопок
        int btnY = 0;

        // Кнопка Свернуть
        int minX = windowWidth - BUTTON_WIDTH * 3;
        if (currentHover == HoverState.MINIMIZE) glColor3f(0.25f, 0.25f, 0.26f); // Светлее при наведении
        else glColor3f(0.18f, 0.18f, 0.19f); // Обычный цвет
        drawRect(minX, btnY, BUTTON_WIDTH, TITLE_BAR_HEIGHT);
        drawMinimizeIcon(minX, btnY, BUTTON_WIDTH, TITLE_BAR_HEIGHT);

        // Кнопка Развернуть
        int maxX = windowWidth - BUTTON_WIDTH * 2;
        if (currentHover == HoverState.MAXIMIZE) glColor3f(0.25f, 0.25f, 0.26f);
        else glColor3f(0.18f, 0.18f, 0.19f);
        drawRect(maxX, btnY, BUTTON_WIDTH, TITLE_BAR_HEIGHT);
        drawMaximizeIcon(maxX, btnY, BUTTON_WIDTH, TITLE_BAR_HEIGHT);

        // Кнопка Закрыть
        int closeX = windowWidth - BUTTON_WIDTH;
        if (currentHover == HoverState.CLOSE) glColor3f(0.91f, 0.07f, 0.14f); // Красный Windows 11 (#E81123)
        else glColor3f(0.18f, 0.18f, 0.19f);
        drawRect(closeX, btnY, BUTTON_WIDTH, TITLE_BAR_HEIGHT);
        drawCloseIcon(closeX, btnY, BUTTON_WIDTH, TITLE_BAR_HEIGHT);

        // 3. Отрисовка уголка изменения размера
        drawResizeHandle();
    }

    // --- Утилиты для Legacy OpenGL рендеринга ---

    private void drawResizeHandle() {
        // Не рисуем уголок, если окно развернуто на весь экран
        if (glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == GLFW_TRUE) return;

        if (currentHover == HoverState.RESIZE_HANDLE || isResizing) {
            glColor3f(0.6f, 0.6f, 0.6f); // Подсветка при наведении/зажатии
        } else {
            glColor3f(0.3f, 0.3f, 0.3f); // Обычный цвет
        }

        glLineWidth(1.5f);
        glBegin(GL_LINES);
        // Линия 1 (ближе к центру)
        glVertex2f(windowWidth - 12, windowHeight - 2);
        glVertex2f(windowWidth - 2, windowHeight - 12);
        // Линия 2 (ближе к краю)
        glVertex2f(windowWidth - 7, windowHeight - 2);
        glVertex2f(windowWidth - 2, windowHeight - 7);
        glEnd();
    }

    private void drawRect(float x, float y, float w, float h) {
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + w, y);
        glVertex2f(x + w, y + h);
        glVertex2f(x, y + h);
        glEnd();
    }

    private void drawMinimizeIcon(float x, float y, float w, float h) {
        glColor3f(0.8f, 0.8f, 0.8f);
        glLineWidth(1.5f);
        glBegin(GL_LINES);
        glVertex2f(x + 15, y + h / 2 + 5);
        glVertex2f(x + w - 15, y + h / 2 + 5);
        glEnd();
    }

    private void drawMaximizeIcon(float x, float y, float w, float h) {
        glColor3f(0.8f, 0.8f, 0.8f);
        glLineWidth(1.5f);
        boolean isMaximized = glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == GLFW_TRUE;

        if (isMaximized) {
            // Иконка "Восстановить" (два квадрата)
            glBegin(GL_LINE_LOOP);
            glVertex2f(x + 16, y + 14); glVertex2f(x + w - 18, y + 14);
            glVertex2f(x + w - 18, y + h - 10); glVertex2f(x + 16, y + h - 10);
            glEnd();
            glBegin(GL_LINES);
            glVertex2f(x + 18, y + 14); glVertex2f(x + 18, y + 10);
            glVertex2f(x + 18, y + 10); glVertex2f(x + w - 14, y + 10);
            glVertex2f(x + w - 14, y + 10); glVertex2f(x + w - 14, y + h - 14);
            glVertex2f(x + w - 14, y + h - 14); glVertex2f(x + w - 18, y + h - 14);
            glEnd();
        } else {
            // Иконка "Развернуть" (один квадрат)
            glBegin(GL_LINE_LOOP);
            glVertex2f(x + 15, y + 10);
            glVertex2f(x + w - 15, y + 10);
            glVertex2f(x + w - 15, y + h - 10);
            glVertex2f(x + 15, y + h - 10);
            glEnd();
        }
    }

    private void drawCloseIcon(float x, float y, float w, float h) {
        if (currentHover == HoverState.CLOSE) glColor3f(1.0f, 1.0f, 1.0f); // Белый на красном фоне
        else glColor3f(0.8f, 0.8f, 0.8f);

        glLineWidth(1.5f);
        glBegin(GL_LINES);
        glVertex2f(x + 16, y + 10);
        glVertex2f(x + w - 16, y + h - 10);

        glVertex2f(x + w - 16, y + 10);
        glVertex2f(x + 16, y + h - 10);
        glEnd();
    }

    public static void main(String[] args) {
        new CustomTitleBarApp().run();
    }
}