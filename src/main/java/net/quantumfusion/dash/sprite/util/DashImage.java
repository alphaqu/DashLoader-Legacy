package net.quantumfusion.dash.sprite.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBImageWrite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

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

    public NativeImage toUndash() {
        try {
            return NativeImage.read(new ByteArrayInputStream(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
