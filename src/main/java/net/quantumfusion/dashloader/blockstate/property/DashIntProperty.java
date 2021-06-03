package net.quantumfusion.dashloader.blockstate.property;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.IntProperty;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashIntProperty implements DashProperty {

    @Serialize(order = 0)
    public List<Integer> values;

    @Serialize(order = 1)
    public String name;


    public DashIntProperty(@Deserialize("values") List<Integer> values,
                           @Deserialize("name") String name) {
        this.values = values;
        this.name = name;
    }

    public DashIntProperty(IntProperty property) {
        name = property.getName();
        values = new ArrayList<>();
        values.addAll(property.getValues());
    }

    @Override
    public IntProperty toUndash(DashRegistry registry) {
        int lowest = -1;
        int highest = -1;
        for (Integer integer : values) {
            if (integer > highest || highest == -1) {
                highest = integer;
            }
            if (integer < lowest || lowest == -1) {
                lowest = integer;
            }
        }
        return IntProperty.of(name, lowest, highest);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashIntProperty that = (DashIntProperty) o;
        return Objects.equals(values, that.values) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, name);
    }
}
