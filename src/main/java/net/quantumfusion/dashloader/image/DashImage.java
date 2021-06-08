package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.quantumfusion.dashloader.DashException;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.mixin.accessor.NativeImageAccessor;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryUtil.memAddress;

public class DashImage implements Dashable {

    @Serialize(order = 0)
    public final byte[] image;

    public DashImage(NativeImage image) {
        byte[] image1 = null;
        try {
            image1 = image.getBytes();
            final File file = DashLoader.getConfig().resolve("testing.png").toFile();
            file.createNewFile();

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
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer x = stack.mallocInt(1);
            final IntBuffer y = stack.mallocInt(1);
            final IntBuffer channels = stack.mallocInt(1);
            final ByteBuffer buf = stack.malloc(image.length);
            buf.put(image);
            buf.flip();
            final long pointer = STBImage.nstbi_load_from_memory(memAddress(buf), buf.remaining(), memAddress(x), memAddress(y), memAddress(channels), 4);
            if (pointer == 0L) {
                throw new DashException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            return NativeImageAccessor.init(NativeImage.Format.ABGR, x.get(0), y.get(0), true, pointer);
        }
    }


}
