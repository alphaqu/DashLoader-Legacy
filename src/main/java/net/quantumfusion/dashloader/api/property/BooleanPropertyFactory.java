package net.quantumfusion.dashloader.api.property;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstate.property.DashBooleanProperty;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashBooleanValue;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;

public class BooleanPropertyFactory implements PropertyFactory {
    @Override
    public DashProperty toDash(Property property, DashRegistry registry, Integer valuePointer) {
        return new DashBooleanProperty((BooleanProperty) property);
    }

    @Override
    public DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, Integer typePointer) {
        return new DashBooleanValue((Boolean) comparable);
    }

    @Override
    public Class<? extends Property<?>> getType() {
        return BooleanProperty.class;
    }

    @Override
    public Class<? extends DashProperty> getDashType() {
        return DashBooleanProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashValueType() {
        return DashBooleanValue.class;
    }

}
