package net.quantumfusion.dashloader.blockstate.property;

import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;

import java.lang.invoke.MethodHandle;

public interface DashProperty extends Factory<Property<?>> {
    Property<?> toUndash(DashRegistry registry);

    @Override
    default FactoryType getFactoryType() {
        return FactoryType.PROPERTY;
    }

    default MethodHandle overrideMethodHandleForValue() {
        return null;
    }

}
