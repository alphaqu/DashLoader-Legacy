package net.quantumfusion.dashloader.cache.blockstates.properties;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.quantumfusion.dashloader.cache.blockstates.properties.value.DashPropertyValue;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public interface DashProperty {
    <T extends Enum<T> & StringIdentifiable> Property<?> toUndash();

}
