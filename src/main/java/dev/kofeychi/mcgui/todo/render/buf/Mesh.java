package dev.kofeychi.mcgui.todo.render.buf;

import dev.kofeychi.mcgui.todo.render.buf.format.VertexElement;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Mesh {
    private final int vao;
    private final int vbo;
    private final int vertexCount;
    private final int mode;

    public Mesh(BuiltBuffer buffer) {
        this.vertexCount = buffer.vertexCount();
        this.mode = buffer.mode();

        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.nglBufferData(GL15.GL_ARRAY_BUFFER, buffer.sizeInBytes(), buffer.address(), GL15.GL_STATIC_DRAW);

        int offset = 0;
        int stride = buffer.format().getStride();
        for (VertexElement element : buffer.format().getElements()) {
            GL20.glEnableVertexAttribArray(element.index());
            GL20.glVertexAttribPointer(
                    element.index(),
                    element.count(),
                    element.type().glType,
                    element.normalized(),
                    stride,
                    offset
            );
            offset += element.getSize();
        }

        GL30.glBindVertexArray(0);
        buffer.close();
    }

    public void draw() {
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(mode, 0, vertexCount);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);
    }
}