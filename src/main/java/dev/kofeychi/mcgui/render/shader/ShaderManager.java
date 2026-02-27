package dev.kofeychi.mcgui.render.shader;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

public class ShaderManager {
    private final Object2ObjectRBTreeMap<String,Shader> shaders = new Object2ObjectRBTreeMap<>();

    public ShaderReference load(String name,String source,ShaderType type) {
        shaders.put(name,new Shader(type,source));
        return new ShaderReference(this,name);
    }

    public void clean() {
        shaders.forEach((k,v)-> v.clean());
        shaders.clear();
    }

    public Shader getShader(String name){
        return shaders.get(name);
    }
}
