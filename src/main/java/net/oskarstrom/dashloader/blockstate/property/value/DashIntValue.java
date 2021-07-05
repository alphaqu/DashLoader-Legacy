package net.oskarstrom.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;

@DashObject(Integer.class)
public class DashIntValue implements DashPropertyValue {
    @Serialize(order = 0)
    public final Integer value;

    @DashConstructor(ConstructorMode.OBJECT)
    public DashIntValue(@Deserialize("value") Integer value) {
        this.value = value;
    }


    @Override
    public Integer toUndash(DashRegistry registry) {
        return value;
    }
}
