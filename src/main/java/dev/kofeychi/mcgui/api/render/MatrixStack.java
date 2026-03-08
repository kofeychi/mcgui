package dev.kofeychi.mcgui.api.render;

import dev.kofeychi.mcgui.impl.render.MatrixStackImpl;
import org.joml.Matrix4f;

public interface MatrixStack {
    static MatrixStack create() {
        return new MatrixStackImpl();
    }

    void push();

    Entry peek();

    void pop();

    int depth();

    boolean isEmpty();

    interface Entry {
        static Entry empty() {
            return new MatrixStackImpl.MatrixStackEntryImpl();
        }

        Matrix4f getPositionMatrix();

        Entry copy();

        void copyFrom(Entry e);
    }
}
