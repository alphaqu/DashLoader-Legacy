package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;

import java.util.Map;

public class RegistryPropertyValueData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "values")
    public Int2ObjectSortedMap<DashPropertyValue> propertyValues;

    public RegistryPropertyValueData(@Deserialize("propertyValues") Int2ObjectSortedMap<DashPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }
}
