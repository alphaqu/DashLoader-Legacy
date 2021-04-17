package net.quantumfusion.dashloader.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.BooleanProperty;
import org.apache.commons.lang3.tuple.MutablePair;

public class DashBooleanProperty implements DashProperty {

    @Serialize(order = 0)
    public boolean value;

    @Serialize(order = 1)
    public String name;

    public DashBooleanProperty(@Deserialize("value") boolean value,
                               @Deserialize("name") String name) {
        this.name = name;
        this.value = value;
    }


    public DashBooleanProperty(BooleanProperty property, boolean value) {
        name = property.getName();
        this.value = value;
    }

    public MutablePair<BooleanProperty, Boolean> toUndash() {
        MutablePair<BooleanProperty, Boolean> out = new MutablePair<>();
        out.setLeft(BooleanProperty.of(name));
        out.setRight(value);
        return out;
    }
}
