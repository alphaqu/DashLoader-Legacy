package net.quantumfusion.dash.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.BooleanProperty;
import org.apache.commons.lang3.tuple.MutablePair;

public class DashBooleanProperty implements DashProperty {

    @Serialize(order = 0)
    public boolean value;

    @Serialize(order = 1)
    public String propertyType;

    @Serialize(order = 2)
    public String name;

    public DashBooleanProperty(@Deserialize("value") boolean value,
                               @Deserialize("propertyType") String propertyType,
                               @Deserialize("name") String name) {
        this.propertyType = propertyType;
        this.name = name;
        this.value = value;
    }


    public DashBooleanProperty(BooleanProperty property, String value) {
        propertyType = property.getType().toString();
        name = property.getName();
        this.value = Boolean.parseBoolean(value);
    }

    public MutablePair<BooleanProperty, Boolean> toUndash() {
        MutablePair<BooleanProperty, Boolean> out = new MutablePair<>();
        out.setLeft(BooleanProperty.of(name));
        out.setRight(value);
        return out;
    }
}
