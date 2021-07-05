package net.quantumfusion.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashConstructor;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.api.enums.ConstructorMode;

@DashObject(Boolean.class)
public class DashBooleanValue implements DashPropertyValue {
    @Serialize(order = 0)
    public final Boolean value;

    @DashConstructor(ConstructorMode.OBJECT)
    public DashBooleanValue(@Deserialize("value") Boolean value) {
        this.value = value;
    }


    @Override
    public Boolean toUndash(DashRegistry registry) {
        return value;
    }
}
