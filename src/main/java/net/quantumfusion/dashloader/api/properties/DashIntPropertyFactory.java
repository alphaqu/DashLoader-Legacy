package net.quantumfusion.dashloader.api.properties;

import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstates.properties.DashIntProperty;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashIntValue;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

public class DashIntPropertyFactory implements DashPropertyFactory {
    public DashIntPropertyFactory() {
    }

    @Override
    public <K> DashProperty toDash(Property<?> property, DashRegistry registry, K var1) {
        return new DashIntProperty((IntProperty) property);
    }

    @Override
    public <K> DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, K var1) {
        return new DashIntValue((Integer) comparable);
    }

    @Override
    public Class<? extends Property> getPropertyType() {
        return IntProperty.class;
    }

    @Override
    public Class<? extends DashProperty> getDashPropertyType() {
        return DashIntProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashPropertyValueType() {
        return DashIntValue.class;
    }
}
