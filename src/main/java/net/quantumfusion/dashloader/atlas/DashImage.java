package net.quantumfusion.dashloader.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.NativeImageAccessor;
import net.quantumfusion.dashloader.util.Dashable;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DashImage implements Dashable {

    @Serialize(order = 0)
    public final byte[] image;

    public DashImage(NativeImage image) {
        byte[] image1 = null;
        try {
            image1 = image.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.image = image1;
    }

    public DashImage(@Deserialize("image") byte[] image) {
        this.image = image;
    }

    /**
     * <h2>I can bet that next dashloader version will change this again. This method needs some serious over engineering.</h2>
     *
     * @param registry da registry
     * @return da image
     */
    @Override
    public final NativeImage toUndash(final DashRegistry registry) {
        final MemoryStack memoryStack = MemoryStack.stackPush();
        final IntBuffer x = memoryStack.mallocInt(1);
        final IntBuffer y = memoryStack.mallocInt(1);
        final IntBuffer channels = memoryStack.mallocInt(1);
        final ByteBuffer buf = MemoryUtil.memAlloc(image.length);
        buf.put(image);
        buf.flip();
        final ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buf, x, y, channels, 4);
        if (imageBuffer == null) {
            try {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final NativeImage imageOut = NativeImageAccessor.init(NativeImage.Format.ABGR, x.get(0), y.get(0), true, MemoryUtil.memAddress(imageBuffer));
        try {
            memoryStack.close();
        } catch (Throwable t) {
            Throwable throwable = null;
            throwable.addSuppressed(t);
        }
        return imageOut;
    }


}
