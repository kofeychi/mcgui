package dev.kofeychi.mcgui.api.render.shader;

import dev.kofeychi.mcgui.impl.render.shader.ManagerImpl;

public interface Manager extends AutoCloseable {
    static Manager empty() {
        return new ManagerImpl();
    }

    Reference load(String name,Program shader);

    CompiledProgram get(int id);

    CompiledProgram get(String name);

    void close();
}
