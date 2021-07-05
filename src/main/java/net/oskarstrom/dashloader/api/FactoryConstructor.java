package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class FactoryConstructor {
    private final ConstructorMode constructorMode;
    private final MethodHandle constructor;

    public FactoryConstructor(ConstructorMode constructorMode, MethodHandle constructor) {
        this.constructorMode = constructorMode;
        this.constructor = constructor;
    }

    public static FactoryConstructor createConstructor(ConstructorMode mode, Class<?> dashClass, Class<?> rawClass) throws NoSuchMethodException, IllegalAccessException {
        final MethodHandle constructor = MethodHandles.publicLookup().findConstructor(dashClass, MethodType.methodType(void.class, mode.getParameterGetter().apply(rawClass)));
        return new FactoryConstructor(mode, constructor);
    }

    public Object createObject(Object originalObject, DashRegistry dashRegistry, Object extraParameters1, Object extraParameters2, Object extraParameters3) {
        try {
            Object object = null;
            switch (constructorMode) {
                case FULL -> object = constructor.invoke(originalObject, dashRegistry, new ExtraVariables(extraParameters1, extraParameters2, extraParameters3));
                case DEFAULT_PARAMETERS -> object = constructor.invoke(originalObject, dashRegistry);
                case OBJECT -> object = constructor.invoke(originalObject);
                case OBJECT_EXTRA -> object = constructor.invoke(originalObject, new ExtraVariables(extraParameters1, extraParameters2, extraParameters3));
                case EMPTY -> object = constructor.invoke();
            }
            if (object != null) {
                return object;
            } else {
                DashLoader.LOGGER.error("Could not create Object from {} factory.", originalObject.getClass().getSimpleName());
                return null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public Object createObject(Object originalObject, DashRegistry dashRegistry, Object extraParameters1, Object extraParameters2) {
        return createObject(originalObject, dashRegistry, extraParameters1, extraParameters2, null);
    }

    public Object createObject(Object originalObject, DashRegistry dashRegistry, Object extraParameters1) {
        return createObject(originalObject, dashRegistry, extraParameters1, null, null);
    }

    public Object createObject(Object originalObject, DashRegistry dashRegistry) {
        return createObject(originalObject, dashRegistry, null, null, null);
    }
}
