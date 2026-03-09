package dev.kofeychi.mcgui.impl.render.texture;

import dev.kofeychi.mcgui.api.render.texture.TextureOptions;
import dev.kofeychi.mcgui.api.render.texture.TextureSource;
import dev.kofeychi.mcgui.api.render.texture.UploadedTexture;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureSourceImpl implements TextureSource {
    private final InputStream source;
    private final TextureOptions options;

    public TextureSourceImpl(InputStream source, TextureOptions options) {
        this.source = source;
        this.options = options;
    }

    @Override
    public TextureOptions options() {
        return options;
    }

    @Override
    public UploadedTexture upload() {
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            var bytes = source.readAllBytes();
            var b = stack.malloc(bytes.length);
            b.put(0, bytes);

            // Обязательно переворачиваем текстуру, т.к. в OpenGL ось Y идет снизу вверх
            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = stbi_load_from_memory(b, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture: " + stbi_failure_reason());
            }

            int width = w.get(0);
            int height = h.get(0);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            options.apply();
            stbi_image_free(image); // Очищаем память STB

            return new UploadedTextureImpl(this,id, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
