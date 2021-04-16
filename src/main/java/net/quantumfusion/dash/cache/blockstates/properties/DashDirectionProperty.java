package net.quantumfusion.dash.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.MutablePair;

public class DashDirectionProperty implements DashProperty {

    @Serialize(order = 0)
    public int direction;

    @Serialize(order = 1)
    public String name;

    public DashDirectionProperty(@Deserialize("direction") int direction,
                                 @Deserialize("name") String name) {
        this.name = name;
        this.direction = direction;
    }

    public DashDirectionProperty(DirectionProperty property, Direction direction) {
        name = property.getName();
        this.direction = direction.getId();
    }

    public MutablePair<DirectionProperty, Direction> toUndash() {
        MutablePair<DirectionProperty, Direction> out = new MutablePair<>();
        out.setLeft(DirectionProperty.of(name, Direction.values()));
        out.setRight(Direction.byId(direction));
        return out;
    }
}
