package net.quantumfusion.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashConstructor;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.api.enums.ConstructorMode;

@DashObject(Direction.class)
public class DashDirectionValue implements DashPropertyValue {
    @Serialize(order = 0)
    public final byte direction;

    public DashDirectionValue(@Deserialize("direction") byte direction) {
        this.direction = direction;
    }

    @DashConstructor(ConstructorMode.OBJECT)
    public DashDirectionValue(Direction direction) {
        this.direction = (byte) direction.getId();
    }


    @Override
    public Direction toUndash(DashRegistry registry) {
        return Direction.byId(direction);
    }
}
