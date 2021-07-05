package net.oskarstrom.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.ExtraVariables;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;

@DashObject(Enum.class)
public class DashEnumValue implements DashPropertyValue {
    @Serialize(order = 0)
    public final String value;

    @Serialize(order = 1)
    public final int enumPointer;

    public DashEnumValue(@Deserialize("value") String value,
                         @Deserialize("enumPointer") int enumPointer) {
        this.value = value;
        this.enumPointer = enumPointer;
    }

    @DashConstructor(ConstructorMode.OBJECT_EXTRA)
    public DashEnumValue(Enum<?> enuum, ExtraVariables propertyPointer) {
        this(enuum.name(), (Integer) propertyPointer.getExtraVariable1());
    }

    @Override
    public Enum<?> toUndash(DashRegistry registry) {
        return get(registry);
    }

    public <T extends Enum<T>> T get(DashRegistry registry) {
        return Enum.valueOf((Class<T>) registry.propertiesOut.get(enumPointer).getType(), value);
    }
}
