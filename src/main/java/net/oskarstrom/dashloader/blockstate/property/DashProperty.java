package net.oskarstrom.dashloader.blockstate.property;

import net.minecraft.state.property.Property;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.Factory;
import net.oskarstrom.dashloader.api.FactoryConstructor;

public interface DashProperty extends Factory<Property<?>> {
	Property<?> toUndash(DashRegistry registry);

	default FactoryConstructor overrideMethodHandleForValue() {
		return null;
	}

}
