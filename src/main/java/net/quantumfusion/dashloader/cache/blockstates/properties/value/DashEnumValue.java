package net.quantumfusion.dashloader.cache.blockstates.properties.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.blockstates.properties.DashEnumProperty;

public class DashEnumValue implements DashPropertyValue{
    @Serialize(order = 0)
    public final String value;

    @Serialize(order = 1)
    public final long enumPointer;

    public DashEnumValue(@Deserialize("value") String value,
                         @Deserialize("enumPointer") long enumPointer) {
        this.value = value;
        this.enumPointer = enumPointer;
    }


    @Override
    public <K extends Comparable> K toUndash(DashRegistry registry) {
        return (K) Enum.valueOf(((Class)((DashEnumProperty)registry.properties.get(enumPointer)).type),value);
    }
}
