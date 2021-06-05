package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;

import java.util.Map;

public class RegistryPropertyData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "properties")
    public Int2ObjectSortedMap<DashProperty> property;

    public RegistryPropertyData(@Deserialize("property") Int2ObjectSortedMap<DashProperty> property) {
        this.property = property;
    }
}
