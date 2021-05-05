package net.quantumfusion.dashloader.blockstates.properties.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.DashRegistry;

public class DashBooleanValue implements DashPropertyValue {
    @Serialize(order = 0)
    public Boolean value;

    public DashBooleanValue(@Deserialize("value") Boolean value) {
        this.value = value;
    }

    @Override
    public <K extends Comparable> K toUndash(DashRegistry registry) {
        return (K) value;
    }
}
