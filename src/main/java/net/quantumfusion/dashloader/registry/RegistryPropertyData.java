package net.quantumfusion.dashloader.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;

import java.util.Map;

public class RegistryPropertyData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "properties")
    public Map<Long, DashProperty> property;

    public RegistryPropertyData(@Deserialize("property") Map<Long, DashProperty> property) {
        this.property = property;
    }
}
