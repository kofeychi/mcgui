package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.api.render.MatrixStack;
import dev.kofeychi.mcgui.todo.render.buf.BuiltBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface Builder extends AutoCloseable {
    Builder vertex(float x, float y, float z);

    Builder texture(float u, float v);

    Builder normal(float x, float y, float z);

    Builder color(float r, float g, float b, float a);

    default Builder vertex(Matrix4f mat,float x, float y, float z) {
        var v = mat.transformPosition(new Vector3f(x,y,z));
        return vertex(v.x,v.y,v.z);
    }

    default Builder vertex(MatrixStack.Entry e, float x, float y, float z) {
        return vertex(e.getPositionMatrix(),x,y,z);
    }

    void push();

    BuiltBuffer build();

    void close();
}
