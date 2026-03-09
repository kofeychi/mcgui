package dev.kofeychi.mcgui.impl.render.vertex;

import dev.kofeychi.mcgui.api.render.vertex.Built;
import dev.kofeychi.mcgui.api.render.vertex.Mesh;
import dev.kofeychi.mcgui.api.render.vertex.format.DrawMode;
import dev.kofeychi.mcgui.api.render.vertex.format.Element;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class MeshImpl implements Mesh {
    private final Built built;
    private int vao;
    private int vbo;
    private final int vertexCount;
    private DrawMode mode;

    public MeshImpl(Built built) {
        this.built = built;
        vertexCount = built.vertexCount();
        mode = DrawMode.TRIANGLES;
    }

    @Override
    public int vao() {
        return vao;
    }

    @Override
    public int vbo() {
        return vbo;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public Built built() {
        return built;
    }

    @Override
    public void upload() {

        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.nglBufferData(GL15.GL_ARRAY_BUFFER, built.sizeBytes(), built.address(), GL15.GL_STATIC_DRAW);

        int offset = 0;
        int stride = built.format().getStride();
        for (Element element : built.format().getElements()) {
            GL20.glEnableVertexAttribArray(element.getIndex());
            GL20.glVertexAttribPointer(
                    element.getIndex(),
                    element.getCount(),
                    element.getType().glType,
                    element.isNormalized(),
                    stride,
                    offset
            );
            offset += element.size();
        }

        GL30.glBindVertexArray(0);
        built.close();
    }

    @Override
    public void draw() {
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(mode.glType(), 0, vertexCount);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void close() {
        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);
    }

    @Override
    public DrawMode mode() {
        return mode;
    }

    @Override
    public void setMode(DrawMode mode) {
        this.mode = mode;
    }
}
