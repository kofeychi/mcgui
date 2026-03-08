package dev.kofeychi.mcgui.impl.render.vertex;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.vertex.Builder;
import dev.kofeychi.mcgui.api.render.vertex.BuilderSource;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class BuilderSourceImpl implements BuilderSource {
    private final Object2ObjectOpenHashMap<CompiledProgram,Builder> builders = new Object2ObjectOpenHashMap<>();

    @Override
    public Builder getBuilder(CompiledProgram program) {
        return builders.computeIfAbsent(
                program,
                id -> Builder.from(256,program.format())
        );
    }

    @Override
    public void draw() {
        for (Map.Entry<CompiledProgram, Builder> builder : builders.entrySet()) {
            var b = builder.getValue();
            var sh = builder.getKey();
            var m = b.build().toMesh();
            m.upload();
            sh.bind();
            m.draw();
            sh.unbind();
            m.close();
        }
        builders.clear();
    }

    @Override
    public void close() {
        for (Builder builder : builders.values()) {
            builder.close();
        }
    }

    @Override
    public void close(Builder builder) {
        builders.values().remove(builder);
    }
}
