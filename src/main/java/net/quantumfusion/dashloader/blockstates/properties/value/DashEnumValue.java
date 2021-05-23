package net.quantumfusion.dashloader.blockstates.properties.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.DashRegistry;

public class DashEnumValue implements DashPropertyValue {
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
    public Enum<?> toUndash(DashRegistry registry) {
        return get(registry);
    }

    public <T extends Enum<T>> T get(DashRegistry registry) {
        return Enum.valueOf((Class<T>) registry.propertiesOut.get(enumPointer).getType(), value);
    }
}
