package dev.kofeychi.mcgui.impl.render;

import dev.kofeychi.mcgui.api.render.Context;
import dev.kofeychi.mcgui.api.render.MatrixStack;
import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.vertex.Builder;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import dev.kofeychi.mcgui.util.Color;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class ContextImpl implements Context {
    protected final CompiledProgram program;
    protected final MatrixStack matrices;
    protected Builder source;

    public ContextImpl(BuilderSource source, CompiledProgram program) {
        this.program = program;
        this.matrices = MatrixStack.create();
        this.source = source.getBuilder(program);
    }

    @Override
    public MatrixStack getMatrices() {
        return matrices;
    }

    @Override
    public void fill(int x, int y, int x1, int y1,int z, int c) {
        source.vertex(matrices.peek(), x, y,z).color(c).push();
        for (int i = 0; i < 2; i++) {
            source.vertex(matrices.peek(), x, y1,z).color(c).push();
            source.vertex(matrices.peek(), x1, y,z).color(c).push();
        }
        source.vertex(matrices.peek(), x1, y1,z).color(c).push();
    }

    @Override
    public void fill(int x, int y, int x1, int y1,int z, Color c) {
        fill(x,y,x1,y1,z,c.getColor());
    }

    @Override
    public void texture(int x, int y, int x1, int y1,int z,int u,int v,int u1,int v1) {
        source.vertex(matrices.peek(), x , y1 ,z).texture(u ,v).push();
        for (int i = 0; i < 2; i++) {
            source.vertex(matrices.peek(), x , y,z).texture(u ,v1).push();
            source.vertex(matrices.peek(), x1, y1,z).texture(u1 ,v).push();
        }
        source.vertex(matrices.peek(), x1, y ,z).texture(u1 ,v1).push();
    }

    @Override
    public void draw() {
        if(source == null) {
            throw new IllegalStateException("Nothing to draw");
        }
        var m = source.buildToMesh();
        m.draw();
        m.close();
        source.close();
        source = Builder.from(256,program.format());
    }

    @Override
    public void invalidateCache() {
        throw new UnsupportedOperationException("Not supported for this context");
    }
}
