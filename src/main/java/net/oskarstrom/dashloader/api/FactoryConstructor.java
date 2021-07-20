package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.api.enums.DashDataType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class FactoryConstructor {
	public final DashDataType dashDataType;
	private final ConstructorMode constructorMode;
	private final MethodHandle constructor;

	public FactoryConstructor(DashDataType dashDataType, ConstructorMode constructorMode, MethodHandle constructor) {
		this.dashDataType = dashDataType;
		this.constructorMode = constructorMode;
		this.constructor = constructor;
	}

	public static FactoryConstructor createConstructor(DashDataType dashDataType, ConstructorMode mode, Class<?> dashClass, Class<?> rawClass) throws NoSuchMethodException, IllegalAccessException {
		final MethodHandle constructor = MethodHandles.publicLookup().findConstructor(dashClass, MethodType.methodType(void.class, mode.getParameterGetter().apply(rawClass)));
		return new FactoryConstructor(dashDataType, mode, constructor);
	}

	public <DO, O> DO createObject(O originalObject, DashRegistry dashRegistry, Object[] extraParameters1) {
		try {
			Object object = null;
			switch (constructorMode) {
				case FULL -> object = constructor.invoke(originalObject, dashRegistry, new ExtraVariables(extraParameters1[0], extraParameters1[1], extraParameters1[2]));
				case DEFAULT_PARAMETERS -> object = constructor.invoke(originalObject, dashRegistry);
				case OBJECT -> object = constructor.invoke(originalObject);
				case OBJECT_EXTRA -> object = constructor.invoke(originalObject, new ExtraVariables(extraParameters1[0], extraParameters1[1], extraParameters1[2]));
				case EMPTY -> object = constructor.invoke();
			}
			if (object != null) {
				return (DO) object;
			} else {
				DashLoader.LOGGER.error("Could not create Object from {} factory.", originalObject.getClass().getSimpleName());
				return null;
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

}
