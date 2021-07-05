package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.oskarstrom.dashloader.blockstate.property.value.DashPropertyValue;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;

public class RegistryPropertyValueData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "values")
    public final Pointer2ObjectMap<DashPropertyValue> propertyValues;

    public RegistryPropertyValueData(@Deserialize("propertyValues") Pointer2ObjectMap<DashPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public Int2ObjectMap<DashPropertyValue> toUndash() {
        return propertyValues.convert();
    }
}
