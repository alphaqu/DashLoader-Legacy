package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;

public class RegistryPropertyData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "properties")
    public Pointer2ObjectMap<DashProperty> property;

    public RegistryPropertyData(@Deserialize("property") Pointer2ObjectMap<DashProperty> property) {
        this.property = property;
    }

    public Int2ObjectMap<DashProperty> toUndash() {
        return property.convert();
    }
}
