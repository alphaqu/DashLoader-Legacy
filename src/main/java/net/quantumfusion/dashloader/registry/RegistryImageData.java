package net.quantumfusion.dashloader.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.quantumfusion.dashloader.atlas.DashImage;

import java.util.Map;

public class RegistryImageData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Long, DashImage> images;

    public RegistryImageData(@Deserialize("images") Map<Long, DashImage> images) {
        this.images = images;
    }
}
