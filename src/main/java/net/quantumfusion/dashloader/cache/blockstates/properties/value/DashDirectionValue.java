package net.quantumfusion.dashloader.cache.blockstates.properties.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.cache.DashRegistry;

public class DashDirectionValue implements DashPropertyValue{
    @Serialize(order = 0)
    public Integer direction;

    public DashDirectionValue(@Deserialize("direction") Integer direction) {
        this.direction = direction;
    }

    public DashDirectionValue(Direction direction) {
        this.direction = direction.getId();
    }

    @Override
    public <K extends Comparable> K toUndash(DashRegistry registry) {
        return (K) direction;
    }
}
