package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;

public class RegistryPropertyValueData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "values")
    public Pntr2ObjectMap<DashPropertyValue> propertyValues;

    public RegistryPropertyValueData(@Deserialize("propertyValues") Int2ObjectMap<DashPropertyValue> propertyValues) {
        this.propertyValues = new Pntr2ObjectMap<>(propertyValues);
    }

    public Int2ObjectMap<DashPropertyValue> toUndash() {
        return propertyValues.toUndash();
    }
}
