package net.quantumfusion.dash.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.MutablePair;

public class DashDirectionProperty implements DashProperty {

    @Serialize(order = 0)
    public String direction;

    @Serialize(order = 1)
    public String propertyType;

    @Serialize(order = 2)
    public String name;

    public DashDirectionProperty(@Deserialize("direction") String direction,
                                 @Deserialize("propertyType") String propertyType,
                                 @Deserialize("name") String name) {
        this.propertyType = propertyType;
        this.name = name;
        this.direction = direction;
    }

    public DashDirectionProperty(DirectionProperty property, Direction direction) {
        propertyType = property.getType().toString();
        name = property.getName();
        this.direction = direction.getName();
    }

    public MutablePair<DirectionProperty, Direction> toUndash() {
        MutablePair<DirectionProperty, Direction> out = new MutablePair<>();
        out.setLeft(DirectionProperty.of(name, Direction.values()));
        out.setRight(Direction.byName(direction));
        return out;
    }
}
