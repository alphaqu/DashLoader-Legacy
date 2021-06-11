package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;

public class RegistryPropertyData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "properties")
    public Pntr2ObjectMap<DashProperty> property;

    public RegistryPropertyData(@Deserialize("property") Pntr2ObjectMap<DashProperty> property) {
        this.property = property;
    }

    public RegistryPropertyData(Int2ObjectMap<DashProperty> property) {
        this.property = new Pntr2ObjectMap<>(property);
    }

    public Int2ObjectMap<DashProperty> toUndash() {
        return property.convert();
    }
}
