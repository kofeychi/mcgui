package dev.kofeychi.mcgui.api.render;

import org.joml.Matrix4f;

// dont extend, its an utility class.
public interface Matrices {
    static Matrix4f screen(int screenWidth, int screenHeight) {
        Matrix4f mat = new Matrix4f();
        mat.ortho(0, screenWidth,screenHeight, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        return mat;
    }
}
