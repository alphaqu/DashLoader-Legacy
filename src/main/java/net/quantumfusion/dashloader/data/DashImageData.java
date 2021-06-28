package net.quantumfusion.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.data.registry.RegistryImageData;

public class DashImageData {
    @Serialize(order = 0)
    public final RegistryImageData imageRegistryData;

    public DashImageData(@Deserialize("imageRegistryData") RegistryImageData imageRegistryData) {
        this.imageRegistryData = imageRegistryData;
    }
}
