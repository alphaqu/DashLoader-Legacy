package net.quantumfusion.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.data.registry.RegistryIdentifierData;

public class DashModelData {
    @Serialize(order = 0)
    public final RegistryIdentifierData identifierRegistryData;

    public DashModelData(@Deserialize("identifierRegistryData") RegistryIdentifierData identifierRegistryData) {
        this.identifierRegistryData = identifierRegistryData;
    }
}
