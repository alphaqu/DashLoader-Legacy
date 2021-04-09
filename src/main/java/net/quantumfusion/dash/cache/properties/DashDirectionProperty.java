package net.quantumfusion.dash.cache.properties;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.MutablePair;

public class DashDirectionProperty extends DashProperty{
    String direction;

    public DashDirectionProperty(String type, String name) {
        super(type, name);
    }

    public DashDirectionProperty(DirectionProperty property, Direction direction) {
        super(property);
        this.direction = direction.getName();
    }

    public MutablePair<DirectionProperty,Direction> toUndash() {
        MutablePair<DirectionProperty,Direction> out = new MutablePair<>();
        out.setLeft(DirectionProperty.of(name,Direction.values()));
        out.setRight(Direction.byName(direction));
        return out;
    }
}
