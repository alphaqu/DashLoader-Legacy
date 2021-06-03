package net.quantumfusion.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.DashRegistry;

public class DashIntValue implements DashPropertyValue {
    @Serialize(order = 0)
    public Integer value;

    public DashIntValue(@Deserialize("value") Integer value) {
        this.value = value;
    }

    @Override
    public Comparable toUndash(DashRegistry registry) {
        return value;
    }
}
