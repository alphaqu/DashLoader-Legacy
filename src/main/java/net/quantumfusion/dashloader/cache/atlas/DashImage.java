package net.quantumfusion.dashloader.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.quantumfusion.dashloader.mixin.NativeImageAccessor;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DashImage {

    @Serialize(order = 0)
    public byte[] image;

    public DashImage(NativeImage image) {
        try {
            this.image = image.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DashImage(@Deserialize("image") byte[] image) {
        this.image = image;
    }

    public NativeImage read() throws IOException {
        MemoryStack memoryStack = MemoryStack.stackPush();
        IntBuffer x = memoryStack.mallocInt(1);
        IntBuffer y = memoryStack.mallocInt(1);
        IntBuffer channels = memoryStack.mallocInt(1);
        ByteBuffer buf = ByteBuffer.allocateDirect(image.length);
        buf.put(image, 0, image.length);
        buf.flip();
        ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buf, x, y, channels, 4);
        if (imageBuffer == null) {
            throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
        }
        NativeImage imageOut = NativeImageAccessor.init(NativeImage.Format.ABGR, x.get(0), y.get(0), true, MemoryUtil.memAddress(imageBuffer));
        try {
            memoryStack.close();
        } catch (Throwable t) {
            Throwable throwable = null;
            throwable.addSuppressed(t);
        }
        return imageOut;
    }

    public NativeImage toUndash() {
        try {
            return read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
