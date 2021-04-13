package net.quantumfusion.dash.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
