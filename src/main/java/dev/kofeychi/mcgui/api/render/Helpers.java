package dev.kofeychi.mcgui.api.render;

import org.joml.Matrix4f;
import org.lwjgl.system.Callback;

// dont extend, its an utility class.
public interface Helpers {
    static Matrix4f screen(int screenWidth, int screenHeight,int pixelScale,int near,int far) {
        Matrix4f mat = new Matrix4f();
        var gw = (float)screenWidth/pixelScale;
        var gh = (float)screenHeight/pixelScale;
        mat.identity().ortho(0, gw,gh, 0, near,far);
        return mat;
    }

    static void free(Callback back) {
        if(back != null) back.free();
    }

    static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
}
