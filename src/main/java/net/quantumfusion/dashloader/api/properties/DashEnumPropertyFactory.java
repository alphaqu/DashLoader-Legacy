package net.quantumfusion.dashloader.api.properties;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstates.properties.DashEnumProperty;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashEnumValue;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

public class DashEnumPropertyFactory implements DashPropertyFactory {

    @Override
    public <K> DashProperty toDash(Property<?> property, DashRegistry registry, K var1) {
        return new DashEnumProperty((EnumProperty) property);
    }

    @Override
    public <K> DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, K var1) {
        return new DashEnumValue(((Enum) comparable).name(), (Long) var1);
    }

    @Override
    public Class<? extends Property> getPropertyType() {
        return EnumProperty.class;
    }

    @Override
    public Class<? extends DashProperty> getDashPropertyType() {
        return DashEnumProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashPropertyValueType() {
        return DashEnumValue.class;
    }

}
