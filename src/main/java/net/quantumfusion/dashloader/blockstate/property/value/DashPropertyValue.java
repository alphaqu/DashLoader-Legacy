package net.quantumfusion.dashloader.blockstate.property.value;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;

public interface DashPropertyValue extends Factory<Comparable<?>> {

    Comparable<?> toUndash(DashRegistry registry);
}

