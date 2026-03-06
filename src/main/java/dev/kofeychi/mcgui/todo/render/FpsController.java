package dev.kofeychi.mcgui.todo.render;

import java.util.concurrent.locks.LockSupport;
import java.util.function.IntSupplier;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FpsController {
    private final IntSupplier targetFps;
    private final double targetFrameTime;
    private double lastTime;
    private float deltaTime;

    public FpsController(IntSupplier fpsSupplier) {
        this.targetFps = fpsSupplier;
        this.targetFrameTime = targetFps.getAsInt() > 0 ? 1.0 / targetFps.getAsInt() : 0.0;
        this.lastTime = glfwGetTime();
    }

    public void update() {
        double currentTime = glfwGetTime();
        deltaTime = (float) (currentTime - lastTime);
        lastTime = currentTime;
    }

    public void sync() {
        if (targetFps.getAsInt() <= 0) return;
        double syncTime = lastTime + targetFrameTime;
        while (glfwGetTime() < syncTime) {
            LockSupport.parkNanos(500_000L);
        }
    }

    public float getDeltaTime() { return deltaTime; }
}
