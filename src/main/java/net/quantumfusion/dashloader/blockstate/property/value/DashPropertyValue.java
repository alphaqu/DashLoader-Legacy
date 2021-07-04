package net.quantumfusion.dashloader.blockstate.property.value;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;

public interface DashPropertyValue extends Factory<Comparable<?>> {

    @Override
    default FactoryType getFactoryType() {
        return FactoryType.PROPERTY_VALUE;
    }

    Comparable<?> toUndash(DashRegistry registry);
}

