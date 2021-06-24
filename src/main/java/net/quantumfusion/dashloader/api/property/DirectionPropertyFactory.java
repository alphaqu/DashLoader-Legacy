package net.quantumfusion.dashloader.api.property;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstate.property.DashDirectionProperty;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashDirectionValue;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;

public class DirectionPropertyFactory implements PropertyFactory {

    @Override
    public DashProperty toDash(Property property, DashRegistry registry, Integer valuePointer) {
        return new DashDirectionProperty((DirectionProperty) property);
    }

    @Override
    public DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, Integer typePointer) {
        return new DashDirectionValue((Direction) comparable);
    }

    @Override
    public Class<? extends Property<?>> getType() {
        return DirectionProperty.class;
    }

    @Override
    public Class<? extends DashProperty> getDashType() {
        return DashDirectionProperty.class;
    }

    @Override
    public Class<? extends DashPropertyValue> getDashValueType() {
        return DashDirectionValue.class;
    }

}
