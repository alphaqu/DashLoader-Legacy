package net.quantumfusion.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashObject;

@DashObject(Integer.class)
public class DashIntValue implements DashPropertyValue {
    @Serialize(order = 0)
    public final int value;

    public DashIntValue(@Deserialize("value") int value) {
        this.value = value;
    }

    public DashIntValue(Integer value, DashRegistry registry, Integer propertyPointer) {
        this.value = value;
    }

    @Override
    public Integer toUndash(DashRegistry registry) {
        return value;
    }
}
