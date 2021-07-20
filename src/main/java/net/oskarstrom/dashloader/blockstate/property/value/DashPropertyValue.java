package net.oskarstrom.dashloader.blockstate.property.value;

import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.Factory;

public interface DashPropertyValue extends Factory<Comparable<?>> {

	Comparable<?> toUndash(DashRegistry registry);
}

