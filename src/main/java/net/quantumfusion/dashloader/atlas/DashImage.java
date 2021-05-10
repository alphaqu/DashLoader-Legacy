package net.quantumfusion.dashloader.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.NativeImageAccessor;
import net.quantumfusion.dashloader.util.Dashable;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBufferSafe;

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
        final int[] x = new int[1];
        final int[] y = new int[1];
        final int[] channels_in_file = new int[1];
        final ByteBuffer buf = ByteBuffer.allocateDirect(image.length);
        buf.put(image);
        buf.flip();
        final ByteBuffer imageBuffer = memByteBufferSafe(STBImage.nstbi_load_from_memory(memAddress(buf), buf.remaining(), x, y, channels_in_file, 4), x[0] * y[0] * 4);
        if (imageBuffer == null) {
            try {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return NativeImageAccessor.init(NativeImage.Format.ABGR, x[0], y[0], true, memAddress(imageBuffer));
    }


}
