package net.quantumfusion.dashloader.api.properties;


import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

public interface DashPropertyFactory {
    /**
     * @param property The property, should cast to your own one.
     * @param registry The registry to call values.
     * @param var1     custom values.
     * @param <Long>   custom value.
     * @return A new dash property
     */
    <Long> DashProperty toDash(Property<?> property, DashRegistry registry, Long var1);

    /**
     * @param comparable
     * @param registry
     * @param var1
     * @param <K>
     * @return
     */
    <K> DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, K var1);

    /**
     * @return
     */
    Class<? extends Property> getPropertyType();

    /**
     * @return
     */
    Class<? extends DashProperty> getDashPropertyType();

    Class<? extends DashPropertyValue> getDashPropertyValueType();

}
