package net.quantumfusion.dashloader.blockstate.property;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.IntProperty;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.Objects;

public class DashIntProperty implements DashProperty {

    @Serialize(order = 0)
    public String name;

    @Serialize(order = 1)
    public int lowest;

    @Serialize(order = 2)
    public int highest;


    public DashIntProperty(@Deserialize("name") String name,
                           @Deserialize("lowest") int lowest,
                           @Deserialize("highest") int highest) {
        this.name = name;
        this.lowest = lowest;
        this.highest = highest;
    }

    public DashIntProperty(IntProperty property) {
        name = property.getName();
        this.lowest = -1;
        this.highest = -1;
        for (Integer integer : property.getValues()) {
            if (integer > highest || highest == -1) {
                this.highest = integer;
            }
            if (integer < lowest || lowest == -1) {
                this.lowest = integer;
            }
        }
    }

    @Override
    public IntProperty toUndash(DashRegistry registry) {
        return IntProperty.of(name, lowest, highest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashIntProperty that = (DashIntProperty) o;
        return lowest == that.lowest && highest == that.highest && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lowest, highest);
    }
}
