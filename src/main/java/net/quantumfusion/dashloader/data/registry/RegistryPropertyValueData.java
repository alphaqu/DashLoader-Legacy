package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.Map;

public class RegistryPropertyValueData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "values")
    public Pointer2ObjectMap<DashPropertyValue> propertyValues;

    public RegistryPropertyValueData(@Deserialize("propertyValues") Pointer2ObjectMap<DashPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public Map<Integer, DashPropertyValue> toUndash() {
        return propertyValues.convert();
    }
}
