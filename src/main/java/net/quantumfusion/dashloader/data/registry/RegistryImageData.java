package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.image.DashImage;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.Map;

public class RegistryImageData {
    @Serialize(order = 0)
    public Pointer2ObjectMap<DashImage> images;

    public RegistryImageData(@Deserialize("images") Pointer2ObjectMap<DashImage> images) {
        this.images = images;
    }


    public Map<Integer, DashImage> toUndash() {
        return images.convert();
    }
}
