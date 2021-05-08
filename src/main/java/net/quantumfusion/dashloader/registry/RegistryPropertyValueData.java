package net.quantumfusion.dashloader.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

import java.util.Map;

public class RegistryPropertyValueData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "values")
    public Map<Long, DashPropertyValue> propertyValues;

    public RegistryPropertyValueData(@Deserialize("propertyValues") Map<Long, DashPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }
}
