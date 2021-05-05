package net.quantumfusion.dashloader.api.properties;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstates.properties.DashDirectionProperty;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashDirectionValue;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

public class DashDirectionPropertyFactory implements DashPropertyFactory {
    @Override
    public <K> DashProperty toDash(Property<?> property, DashRegistry registry, K var1) {
        return new DashDirectionProperty((DirectionProperty) property);
    }

    @Override
    public <K> DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, K var1) {
        return new DashDirectionValue((Integer) comparable);
    }

    @Override
    public Class<? extends Property> getPropertyType() {
        return DirectionProperty.class;
    }

    @Override
    public Class<? extends DashProperty> getDashPropertyType() {
        return DashDirectionProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashPropertyValueType() {
        return DashDirectionValue.class;
    }
}
