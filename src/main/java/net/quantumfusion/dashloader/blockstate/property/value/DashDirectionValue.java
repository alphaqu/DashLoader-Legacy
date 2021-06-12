package net.quantumfusion.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;

public class DashDirectionValue implements DashPropertyValue {
    @Serialize(order = 0)
    public byte direction;

    public DashDirectionValue(@Deserialize("direction") byte direction) {
        this.direction = direction;
    }

    public DashDirectionValue(Direction direction) {
        this.direction = (byte) direction.getId();
    }


    @Override
    public Comparable toUndash(DashRegistry registry) {
        return Direction.byId(direction);
    }
}
