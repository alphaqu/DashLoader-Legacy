package net.quantumfusion.dashloader.blockstate.property;

import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryConstructor;

public interface DashProperty extends Factory<Property<?>> {
    Property<?> toUndash(DashRegistry registry);

    default FactoryConstructor overrideMethodHandleForValue() {
        return null;
    }

}
