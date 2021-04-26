package net.quantumfusion.dashloader.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;


import java.util.Objects;

public class DashBooleanProperty implements DashProperty {

    @Serialize(order = 0)
    public String name;

    public DashBooleanProperty(@Deserialize("name") String name) {
        this.name = name;
    }


    public DashBooleanProperty(BooleanProperty property) {
        name = property.getName();
    }

    @Override
    public BooleanProperty toUndash() {
        return BooleanProperty.of(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashBooleanProperty that = (DashBooleanProperty) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
