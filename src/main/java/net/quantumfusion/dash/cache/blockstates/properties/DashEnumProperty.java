package net.quantumfusion.dash.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class DashEnumProperty implements DashProperty {

    @Serialize(order = 0)
    public String value;

    @Serialize(order = 1)
    public List<String> values;

    @Serialize(order = 2)
    public String propertyType;

    @Serialize(order = 3)
    public String name;

    public DashEnumProperty(@Deserialize("value") String value,
                            @Deserialize("values")  List<String> values,
                            @Deserialize("propertyType") String propertyType,
                            @Deserialize("name")  String name) {
        this.value = value;
        this.values = values;
        this.propertyType = propertyType;
        this.name = name;
    }

    public DashEnumProperty(EnumProperty property, Enum value) {
        propertyType = property.getType().toString();
        name = property.getName();
        values = new ArrayList<>();
        property.getValues().forEach(valuee -> values.add(valuee.toString()));
        this.value = value.name();
    }

    public  <T extends Enum<T> & StringIdentifiable> MutablePair<EnumProperty<T> , Enum<T>> toUndash() {
        MutablePair<EnumProperty<T>, Enum<T>> out = new MutablePair<>();
        try {
            Class<T> type = (Class<T>) Class.forName(this.propertyType.replaceFirst("class ", ""));
            out.setLeft(EnumProperty.of(name, type));
            out.setRight(Enum.valueOf(type, value));
            return out;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
