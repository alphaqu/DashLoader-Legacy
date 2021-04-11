package net.quantumfusion.dash.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.IntProperty;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class DashIntProperty implements DashProperty {

    @Serialize(order = 0)
    public List<Integer> values;

    @Serialize(order = 1)
    public int value;

    @Serialize(order = 2)
    public String propertyType;

    @Serialize(order = 3)
    public String name;


    public DashIntProperty(@Deserialize("values") List<Integer> values,
                           @Deserialize("value")   int value,
                           @Deserialize("propertyType")  String propertyType,
                           @Deserialize("name")   String name) {
        this.values = values;
        this.value = value;
        this.propertyType = propertyType;
        this.name = name;
    }

    public DashIntProperty(IntProperty property, String value) {
        propertyType = property.getType().toString();
        name = property.getName();
        values = new ArrayList<>();
        values.addAll(property.getValues());
        this.value = Integer.parseInt(value);
    }

    public MutablePair<IntProperty, Integer> toUndash() {
        MutablePair<IntProperty, Integer> out = new MutablePair<>();
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
        out.setLeft(IntProperty.of(name, lowest, highest));
        out.setRight(value);
        return out;
    }


}
