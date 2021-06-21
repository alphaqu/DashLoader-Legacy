package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.image.DashImage;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

public class RegistryImageData {
    @Serialize(order = 0)
    public Pointer2ObjectMap<DashImage> images;

    public RegistryImageData(@Deserialize("images") Pointer2ObjectMap<DashImage> images) {
        this.images = images;
    }


    public Int2ObjectMap<DashImage> toUndash() {
        return images.convert();
    }
}
