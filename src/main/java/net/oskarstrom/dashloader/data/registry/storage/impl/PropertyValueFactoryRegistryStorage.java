package net.oskarstrom.dashloader.data.registry.storage.impl;

import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.enums.DashDataType;
import net.oskarstrom.dashloader.blockstate.property.value.DashEnumValue;
import net.oskarstrom.dashloader.blockstate.property.value.DashPropertyValue;
import net.oskarstrom.dashloader.data.registry.storage.FactoryRegistryStorage;

public class PropertyValueFactoryRegistryStorage extends FactoryRegistryStorage<Comparable<?>, DashPropertyValue> {
    public PropertyValueFactoryRegistryStorage(Class<?> originalObjectClass, DashRegistry registry, DashDataType type) {
        super(originalObjectClass, registry, type);
    }

    @Override
    public int register(Comparable<?> originalObject) {
        final int ptr = originalObject.hashCode();
        if (missing(ptr)) {
            if (originalObject instanceof Enum<?> enumObject) {
                registerDashObject(ptr, new DashEnumValue(enumObject));
                return ptr;
            }
            return super.register(originalObject);
        }
        return ptr;
    }
}
