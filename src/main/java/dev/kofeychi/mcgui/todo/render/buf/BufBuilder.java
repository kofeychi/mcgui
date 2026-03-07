package dev.kofeychi.mcgui.todo.render.buf;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface BufBuilder {
    BufBuilder vertex(float x, float y, float z);

    default BufBuilder vertex(Matrix4f mat, float x, float y, float z) {
        var vec = new Vector3f(x, y, z);
        mat.transformPosition(vec);
        return vertex(vec.x, vec.y, vec.z);
    }

    BufBuilder texture(float u, float v);

    BufBuilder normal(float x, float y, float z);

    BufBuilder color(float r, float g, float b, float a);

    BufBuilder push();

    BuiltBuffer build();

    void close();
}
