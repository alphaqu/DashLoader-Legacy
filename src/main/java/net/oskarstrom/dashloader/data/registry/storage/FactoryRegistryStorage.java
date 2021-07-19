package net.oskarstrom.dashloader.data.registry.storage;

import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.Factory;
import net.oskarstrom.dashloader.api.FactoryConstructor;
import net.oskarstrom.dashloader.api.enums.DashDataType;

import java.util.Map;

public class FactoryRegistryStorage<O, D extends Factory<O>> extends AbstractRegistryStorage<O, D> {
    private final Map<Class<?>, FactoryConstructor> factoryMappings;
    private final DashDataType type;

    public FactoryRegistryStorage(Class<?> originalObjectClass, DashRegistry registry, DashDataType type) {
        super.init(registry, originalObjectClass);
        this.factoryMappings = DashLoader.getInstance().getApi().mappings.get(type);
        this.type = type;
    }

    @Override
    public int register(O originalObject) {
        final int ptr = originalObject.hashCode();
        if (missing(ptr)) {
            final D fromFactory = createFromFactory(originalObject);
            if (fromFactory != null) {
                registerDashObject(ptr, fromFactory);
                return ptr;
            }
            return -1;
        }
        return ptr;
    }

    protected <OO> D createFromFactory(OO originalObject, Object... extraVar) {
        FactoryConstructor factory = factoryMappings.get(originalObject.getClass());
        if (factory != null) {
            //ensure length 3
            Object[] extraParametersOut = new Object[3];
            System.arraycopy(extraVar, 0, extraParametersOut, 0, extraVar.length);
            return factory.createObject(originalObject, registry, extraParametersOut);
        } else {
            registry.apiFailed.putIfAbsent(originalObject.getClass(), type);
        }
        return null;
    }
}
