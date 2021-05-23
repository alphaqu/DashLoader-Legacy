package net.quantumfusion.dashloader.api.properties;


import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;

public interface PropertyFactory extends Factory<Property, DashProperty> {
    /**
     * @param property The property, should cast to your own one.
     * @param registry The registry to call values.
     * @param var1     custom values.
     * @param <Long>   custom value.
     * @return A new dash property
     */
    <Long> DashProperty toDash(Property property, DashRegistry registry, Long var1);

    /**
     * @param comparable
     * @param registry
     * @param var1
     * @param <K>
     * @return
     */
    <K> DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, K var1);

    Class<? extends DashPropertyValue> getDashValueType();

    default FactoryType getFactoryType() {
        return FactoryType.PROPERTY;
    }

}
