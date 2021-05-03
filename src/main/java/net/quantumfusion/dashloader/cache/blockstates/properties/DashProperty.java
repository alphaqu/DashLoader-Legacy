package net.quantumfusion.dashloader.cache.blockstates.properties;

import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public interface DashProperty {
    <T extends Enum<T> & StringIdentifiable> Property<?> toUndash();

}
