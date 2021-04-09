package net.quantumfusion.dash.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dash.util.Dashable;

public class DashDirection implements Dashable {
    @Serialize(order = 0)
    public final String name;

    public DashDirection(@Deserialize("name") String name) {
        this.name = name;
    }

    public DashDirection(Direction direction) {
        name = direction.getName();
    }

    public Direction toUndash() {
        return Direction.byName(name);
    }
}
