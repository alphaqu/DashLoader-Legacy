package net.quantumfusion.dashloader.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static net.minecraft.client.texture.NativeImage.Format.*;

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

    public ByteBuffer readAllToByteBuffer(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer2 = MemoryUtil.memAlloc(image.length + 64);
        while (Channels.newChannel(inputStream).read(byteBuffer2) != -1) {
            if (byteBuffer2.remaining() == 0) {
                byteBuffer2 = MemoryUtil.memRealloc(byteBuffer2, byteBuffer2.capacity());
            }
        }

        return byteBuffer2;
    }

    public NativeImage toUndash() {
        try {
            ByteBuffer byteBuffer = null;
            BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(image));
            try {
                byteBuffer = MemoryUtil.memAlloc(image.length + 128);
                final ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                while (readableByteChannel.read(byteBuffer) != -1) {
                    if (byteBuffer.remaining() == 0) {
                        byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity());
                    }
                }
                byteBuffer.rewind();
                return NativeImage.read(NativeImage.Format.ABGR, byteBuffer);
            } finally {
                MemoryUtil.memFree(byteBuffer);
                IOUtils.closeQuietly(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
