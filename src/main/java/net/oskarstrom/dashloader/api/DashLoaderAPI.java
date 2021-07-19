package net.oskarstrom.dashloader.api;

import io.activej.serializer.SerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.api.enums.DashDataType;
import net.oskarstrom.dashloader.blockstate.property.DashBooleanProperty;
import net.oskarstrom.dashloader.blockstate.property.DashDirectionProperty;
import net.oskarstrom.dashloader.blockstate.property.DashEnumProperty;
import net.oskarstrom.dashloader.blockstate.property.DashIntProperty;
import net.oskarstrom.dashloader.blockstate.property.value.DashBooleanValue;
import net.oskarstrom.dashloader.blockstate.property.value.DashDirectionValue;
import net.oskarstrom.dashloader.blockstate.property.value.DashEnumValue;
import net.oskarstrom.dashloader.blockstate.property.value.DashIntValue;
import net.oskarstrom.dashloader.font.DashBitmapFont;
import net.oskarstrom.dashloader.font.DashBlankFont;
import net.oskarstrom.dashloader.font.DashTrueTypeFont;
import net.oskarstrom.dashloader.font.DashUnicodeFont;
import net.oskarstrom.dashloader.model.DashBasicBakedModel;
import net.oskarstrom.dashloader.model.DashBuiltinBakedModel;
import net.oskarstrom.dashloader.model.DashMultipartBakedModel;
import net.oskarstrom.dashloader.model.DashWeightedBakedModel;
import net.oskarstrom.dashloader.model.predicates.DashAndPredicate;
import net.oskarstrom.dashloader.model.predicates.DashOrPredicate;
import net.oskarstrom.dashloader.model.predicates.DashSimplePredicate;
import net.oskarstrom.dashloader.model.predicates.DashStaticPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class DashLoaderAPI {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Class<?>[] defaultParameters = new Class[]{DashRegistry.class};

    public final Map<DashDataType, Map<Class<?>, FactoryConstructor>> mappings;
    public final List<DashDataClass> dataClasses;
    public Map<String, List<Class<?>>> types;
    private boolean initialized = false;

    public DashLoaderAPI() {
        mappings = Collections.synchronizedMap(new HashMap<>());
        types = Collections.synchronizedMap(new HashMap<>());
        dataClasses = Collections.synchronizedList(new ArrayList<>());
    }

    public static FactoryConstructor createConstructor(Class<?> dashClass, Class<?> rawClass, DashDataType dashDataType) throws NoSuchMethodException, IllegalAccessException {
        for (ConstructorMode value : ConstructorMode.values()) {
            try {
                return FactoryConstructor.createConstructor(dashDataType, value, dashClass, rawClass);
            } catch (NoSuchMethodException ignored) {
            }
        }
        //TODO remove in 2.2
        {
            final Constructor<?>[] constructors = dashClass.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(DashConstructor.class)) {
                    final DashConstructor[] dashConstructors = constructor.getAnnotationsByType(DashConstructor.class);
                    if (dashConstructors.length != 0) {
                        final ConstructorMode value = dashConstructors[0].value();
                        try {
                            return FactoryConstructor.createConstructor(dashDataType, value, dashClass, rawClass);
                        } catch (NoSuchMethodException e) {
                            throw new NoSuchMethodException(value.getExpectedMethod(dashClass, rawClass));
                        }
                    }
                }
            }
        }
        throw new NoSuchMethodException(ConstructorMode.DEFAULT_PARAMETERS.getExpectedMethod(dashClass, rawClass));
    }

    private void clearAPI() {
        mappings.clear();
        types.clear();
        dataClasses.clear();
    }

    private void addType(DashDataType type, Class<?> dashClass) {
        types.computeIfAbsent(type.toString(), integer -> new ArrayList<>()).add(dashClass);
    }

    private void addFactoryToType(DashDataType type, Class<?> dashClass, Class<?> targetClass, FactoryConstructor constructor) {
        addType(type, dashClass);
        mappings.computeIfAbsent(type, type1 -> Collections.synchronizedMap(new HashMap<>())).put(targetClass, constructor);
        LOGGER.info("Added custom DashObject: {} {}", type, dashClass.getSimpleName());
    }


    private void addDataObjectToType(DashDataType type, Class<?> dataClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        addType(type, dataClass);
        dataClasses.add((DashDataClass) dataClass.getDeclaredConstructor().newInstance());
        LOGGER.info("Added custom DashDataObject: {}", dataClass.getSimpleName());
    }

    public void applyTypes(SerializerBuilder builder, String internalString) {
        final List<Class<?>> typeList = types.get(internalString);
        if (typeList == null) {
            LOGGER.error("Cannot find {} in typeMap", internalString);
            return;
        }
        typeList.sort(Comparator.comparing(Class::getSimpleName));
        builder.withSubclasses(internalString, typeList);

    }

    private DashDataType getTypeFromFactoryInterface(Class<?> closs) {
        for (DashDataType value : DashDataType.values()) {
            if (value.factoryInterface == closs) {
                return value;
            }
        }
        LOGGER.error("Cannot find Factory Type from {} class parameter.", closs.getSimpleName());
        return null;
    }

    public void registerDashObject(Class<?> dashClass) {
        final Class<?>[] interfaces = dashClass.getInterfaces();
        if (interfaces.length == 0) {
            LOGGER.error("No Interfaces found. Class: {}", dashClass.getSimpleName());
            return;
        }
        final DashObject annotation = dashClass.getDeclaredAnnotation(DashObject.class);
        if (annotation == null) {
            LOGGER.error("Custom DashObject implementation does not have DashObject Annotation. Class: {}", dashClass.getSimpleName());
            return;
        }
        DashDataType type = annotation.overrideType();
        if (type == DashDataType.DEFAULT) {
            type = getTypeFromFactoryInterface(interfaces[0]);
        }
        if (type == null) {
            LOGGER.error("Factory type could not be identified. Class: {}", dashClass.getSimpleName());
            return;
        }
        if (type.requiresTargetObject) {
            if (annotation.value() == NullPointerException.class) {
                LOGGER.error("The type {} requires a target object in the @DashObject annotation", type.name);
                return;
            }
        }
        if (type != DashDataType.DATA) {
            final Class<?> rawClass = annotation.value();
            try {
                addFactoryToType(type, dashClass, rawClass, createConstructor(dashClass, rawClass, type));
            } catch (NoSuchMethodException e) {
                LOGGER.error("Constructor not matching/found. Expected: {}", e.getMessage());
            } catch (IllegalAccessException e) {
                LOGGER.error("Constructor not accessible in {}", dashClass.getSimpleName());
            }
        } else {
            try {
                addDataObjectToType(type, dashClass);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }


    private void initNativeAPI() {
        registerDashObject(DashBasicBakedModel.class);
        registerDashObject(DashBuiltinBakedModel.class);
        registerDashObject(DashMultipartBakedModel.class);
        registerDashObject(DashWeightedBakedModel.class);

        registerDashObject(DashAndPredicate.class);
        registerDashObject(DashOrPredicate.class);
        registerDashObject(DashSimplePredicate.class);
        addType(DashDataType.PREDICATE, DashStaticPredicate.class); // still cursed

        registerDashObject(DashBooleanProperty.class);
        registerDashObject(DashDirectionProperty.class);
        registerDashObject(DashEnumProperty.class);
        registerDashObject(DashIntProperty.class);

        registerDashObject(DashBooleanValue.class);
        registerDashObject(DashDirectionValue.class);
        registerDashObject(DashEnumValue.class);
        registerDashObject(DashIntValue.class);

        registerDashObject(DashBitmapFont.class);
        registerDashObject(DashBlankFont.class);
        registerDashObject(DashTrueTypeFont.class);
        registerDashObject(DashUnicodeFont.class);
    }


    public void initAPI() {
        if (!initialized) {
            Instant start = Instant.now();
            clearAPI();
            initNativeAPI();
            FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
                final ModMetadata metadata = modContainer.getMetadata();
                if (metadata.getCustomValues().size() != 0) {
                    applyForClassesInValue(metadata, "dashloader:customobject", this::registerDashObject);
                }
            });
            LOGGER.info("[" + Duration.between(start, Instant.now()).toMillis() + "ms] Initialized api.");
            initialized = true;
        }
    }

    private void applyForClassesInValue(ModMetadata modMetadata, String valueName, Consumer<Class<?>> func) {
        CustomValue value = modMetadata.getCustomValue(valueName);
        if (value != null) {
            for (CustomValue customValue : value.getAsArray()) {
                final String dashObject = customValue.getAsString();
                try {
                    final Class<?> closs = Class.forName(dashObject);
                    func.accept(closs);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Object not found in mod {}. Value: {}", modMetadata.getId(), customValue);
                }
            }
        }
    }


}