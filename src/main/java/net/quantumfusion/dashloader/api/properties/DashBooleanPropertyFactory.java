package net.quantumfusion.dashloader.api.properties;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstates.properties.DashBooleanProperty;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashBooleanValue;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

public class DashBooleanPropertyFactory implements DashPropertyFactory {
    @Override
    public <K> DashProperty toDash(Property<?> property, DashRegistry registry, K var1) {
        return new DashBooleanProperty((BooleanProperty) property);
    }

    @Override
    public <K> DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, K var1) {
        return new DashBooleanValue((Boolean) comparable);
    }

    @Override
    public Class<? extends Property> getPropertyType() {
        return BooleanProperty.class;
    }

    @Override
    public Class<? extends DashProperty> getDashPropertyType() {
        return DashBooleanProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashPropertyValueType() {
        return DashBooleanValue.class;
    }
}
