package dev.kofeychi.mcgui.impl.render;

import dev.kofeychi.mcgui.api.render.Context;
import dev.kofeychi.mcgui.api.render.MatrixStack;
import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.vertex.Builder;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import dev.kofeychi.mcgui.util.Color;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class ContextImpl implements Context {
    private final CompiledProgram program;
    private final MatrixStack matrices;
    private Builder source;

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
        source.vertex(matrices.peek(), x, y1,z).color(c).push();
        source.vertex(matrices.peek(), x1, y,z).color(c).push();
        source.vertex(matrices.peek(), x1, y1,z).color(c).push();
        draw();
    }

    @Override
    public void fill(int x, int y, int x1, int y1,int z, Color c) {
        fill(x,y,x1,y1,z,c.getColor());
    }

    @Override
    public void draw() {
        if(source == null) {
            throw new IllegalStateException("Nothing to draw");
        }
        var m = source.buildToMesh();
        m.setMode(GL_TRIANGLE_STRIP);
        m.draw();
        source.close();
        source = Builder.from(256,program.format());
    }
}
