package net.quantumfusion.dash.cache.properties;

import net.minecraft.state.property.EnumProperty;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;

public class DashEnumProperty extends DashProperty{
    String value;
    ArrayList<String> values;

    public DashEnumProperty(String type, String name) {
        super(type, name);
    }

    public <T extends Enum> DashEnumProperty(EnumProperty property, T value) {
        super(property);
        values = new ArrayList<>();
        property.getValues().forEach(valuee -> {
            values.add(valuee.toString());
        });
        this.value = value.name();
    }

    public MutablePair<EnumProperty, Enum> toUndash() {
        MutablePair<EnumProperty,Enum> out = new MutablePair<>();
        try {
            Class type = Class.forName(this.type.replaceFirst("class ", ""));
            out.setLeft(EnumProperty.of(name,type));
            out.setRight(Enum.valueOf(type,value));
            return out;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
