package dev.kofeychi.mcgui.api.render.shader;

import org.joml.Vector2ic;
import org.joml.Vector3ic;
import org.joml.Vector4ic;

public interface Uniforms {
    // primitives
    // vectors for primitives
    // matrices
    String getName();

    int programId();

    int location();

    void setInt(int value);

    void setFloat(float value);

    void setIntVector2(int x, int y);

    void setFloatVector2(float x, float y);

    void setIntVector3(int x, int y, int z);

    void setFloatVector3(float x, float y, float z);

    void setIntVector4(int x, int y, int z, int w);

    void setFloatVector4(float x, float y, float z, int w);

    default void setIntVec4(Vector4ic vec) {
        setIntVector4(vec.x(), vec.y(), vec.z(), vec.w());
    }

    default void setFloatVec4(Vector4ic vec) {
        setFloatVector4(vec.x(), vec.y(), vec.z(), vec.w());
    }

    default void setIntVec3(Vector3ic vec) {
        setIntVector3(vec.x(), vec.y(), vec.z());
    }

    default void setFloatVec3(Vector3ic vec) {
        setFloatVector3(vec.x(), vec.y(), vec.z());
    }

    default void setIntVec2(Vector2ic vec) {
        setIntVector2(vec.x(), vec.y());
    }

    default void setFloatVec2(Vector2ic vec) {
        setFloatVector2(vec.x(), vec.y());
    }
}
