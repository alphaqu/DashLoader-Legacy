package net.quantumfusion.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;

public class DashDirection implements Dashable {
    @Serialize(order = 0)
    public final short id;

    public DashDirection(@Deserialize("id") short id) {
        this.id = id;
    }

    public DashDirection(Direction direction) {
        id = (short) direction.getId();
    }

    public Direction toUndash(DashRegistry registry) {
        return Direction.byId(id);
    }
}
