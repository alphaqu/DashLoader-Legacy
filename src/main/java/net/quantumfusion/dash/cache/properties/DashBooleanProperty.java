package net.quantumfusion.dash.cache.properties;

import net.minecraft.state.property.BooleanProperty;
import org.apache.commons.lang3.tuple.MutablePair;

public class DashBooleanProperty extends DashProperty {
    boolean value;

    public DashBooleanProperty(String type, String name) {
        super(type, name);
    }

    public DashBooleanProperty(BooleanProperty property, String value) {
        super(property);
        this.value = Boolean.parseBoolean(value);
    }

    public MutablePair<BooleanProperty, Boolean> toUndash() {
        MutablePair<BooleanProperty, Boolean> out = new MutablePair<>();
        out.setLeft(BooleanProperty.of(name));
        out.setRight(value);
        return out;
    }
}
