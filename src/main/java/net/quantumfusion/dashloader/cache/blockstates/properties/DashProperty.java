package net.quantumfusion.dashloader.cache.blockstates.properties;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Map;

public interface DashProperty {
    <T extends Enum<T> & StringIdentifiable> Map.Entry<? extends Property<?>, ? extends Comparable<?>> toUndash();

}
