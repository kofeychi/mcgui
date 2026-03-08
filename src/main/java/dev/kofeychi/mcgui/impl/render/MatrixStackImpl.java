package dev.kofeychi.mcgui.impl.render;

import dev.kofeychi.mcgui.api.render.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Matrix4f;

public class MatrixStackImpl implements MatrixStack {
    private final ObjectArrayList<Entry> stack = new ObjectArrayList<>();
    private int depth = 0;
    public MatrixStackImpl() {
        this.stack.add(new MatrixStackEntryImpl());
    }

    @Override
    public void push() {
        var entry = peek();
        ++depth;
        if (depth >= stack.size()) {
            stack.add(entry.copy());
        } else {
            stack.get(depth).copyFrom(entry);
        }
    }

    @Override
    public Entry peek() {
        return stack.get(depth);
    }

    @Override
    public void pop() {
        if(depth==0) {
            throw new RuntimeException("cannot pop empty stack");
        } else {
            depth--;
        }
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public boolean isEmpty() {
        return depth==0;
    }

    public static class MatrixStackEntryImpl implements MatrixStack.Entry {
        private final Matrix4f position = new Matrix4f();

        @Override
        public Matrix4f getPositionMatrix() {
            return position;
        }

        public void copyFrom(Entry source) {
            position.set(source.getPositionMatrix());
        }

        public Entry copy() {
            var e = new MatrixStackEntryImpl();
            e.copyFrom(this);
            return e;
        }
    }
}
