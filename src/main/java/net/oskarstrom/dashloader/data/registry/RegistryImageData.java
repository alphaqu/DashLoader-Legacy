package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.image.DashImage;

public class RegistryImageData {
    @Serialize(order = 0)
    public final Pointer2ObjectMap<DashImage> images;

    public RegistryImageData(@Deserialize("images") Pointer2ObjectMap<DashImage> images) {
        this.images = images;
    }


    public Int2ObjectMap<DashImage> toUndash() {
        return images.convert();
    }
}
