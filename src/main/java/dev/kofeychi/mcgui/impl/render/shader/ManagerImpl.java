package dev.kofeychi.mcgui.impl.render.shader;

import dev.kofeychi.mcgui.api.render.shader.CompiledProgram;
import dev.kofeychi.mcgui.api.render.shader.Manager;
import dev.kofeychi.mcgui.api.render.shader.Program;
import dev.kofeychi.mcgui.api.render.shader.Reference;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ManagerImpl implements Manager {
    private final Object2ObjectOpenHashMap<String,CompiledProgram> byName = new Object2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<CompiledProgram> byId = new Int2ObjectOpenHashMap<>();

    @Override
    public Reference load(String name,Program shader) {
        var c = shader.compile();
        byName.put(name,c);
        byId.put(c.id(),c);
        return new ReferenceImpl(c.id(),this);
    }

    @Override
    public CompiledProgram get(int id) {
        return byId.get(id);
    }

    @Override
    public CompiledProgram get(String name) {
        return byName.get(name);
    }

    @Override
    public void close() {
        for(CompiledProgram program : byId.values()) {
            program.close();
        }
    }
}
