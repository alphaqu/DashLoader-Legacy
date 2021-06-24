package net.quantumfusion.dashloader.api.property;


import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;

public interface PropertyFactory extends Factory<Property, DashProperty, Integer> {
    /**
     * @param property     The property, should cast to your own one.
     * @param registry     The registry to call values.
     * @param valuePointer custom value.
     * @return A new dash property
     */
    DashProperty toDash(Property property, DashRegistry registry, Integer valuePointer);

    /**
     * @param comparable
     * @param registry
     * @param typePointer
     * @param <K>
     * @return
     */
    DashPropertyValue toDash(Comparable<?> comparable, DashRegistry registry, Integer typePointer);

    Class<? extends DashPropertyValue> getDashValueType();

    default FactoryType getFactoryType() {
        return FactoryType.PROPERTY;
    }

}
