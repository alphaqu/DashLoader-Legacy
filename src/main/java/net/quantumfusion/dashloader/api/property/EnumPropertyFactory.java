package net.quantumfusion.dashloader.api.property;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstate.property.DashEnumProperty;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashEnumValue;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;

public class EnumPropertyFactory implements PropertyFactory {

    @Override
    public DashProperty toDash(Property property, DashRegistry registry, Integer valuePointer) {
        return new DashEnumProperty((EnumProperty) property);
    }

    @Override
    public DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, Integer typePointer) {
        return new DashEnumValue(((Enum) comparable).name(), typePointer);
    }

    @Override
    public Class<? extends Property> getType() {
        return EnumProperty.class;
    }


    @Override
    public Class<? extends DashProperty> getDashType() {
        return DashEnumProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashValueType() {
        return DashEnumValue.class;
    }

}
