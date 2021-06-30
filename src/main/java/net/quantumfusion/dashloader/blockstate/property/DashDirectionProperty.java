package net.quantumfusion.dashloader.blockstate.property;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.Objects;

public class DashDirectionProperty implements DashProperty {


    @Serialize(order = 0)
    public final String name;

    public DashDirectionProperty(@Deserialize("name") String name) {
        this.name = name;
    }

    public DashDirectionProperty(DirectionProperty property) {
        name = property.getName();
    }

    @Override
    public Property toUndash(DashRegistry registry) {
        return DirectionProperty.of(name, Direction.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashDirectionProperty that = (DashDirectionProperty) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
