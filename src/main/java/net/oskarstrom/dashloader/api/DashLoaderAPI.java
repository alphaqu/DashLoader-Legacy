package net.oskarstrom.dashloader.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.property.Property;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.api.enums.FactoryType;
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
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DashLoaderAPI {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Class<?>[] defaultParameters = new Class[]{DashRegistry.class};

    public final Map<Class<? extends BakedModel>, FactoryConstructor> modelMappings;
    public final Map<Class<? extends Property<?>>, FactoryConstructor> propertyMappings;
    public final Map<Class<? extends Comparable<?>>, FactoryConstructor> propertyValueMappings;
    public final Map<Class<? extends Font>, FactoryConstructor> fontMappings;
    public final Map<Class<? extends MultipartModelSelector>, FactoryConstructor> predicateMappings;
    public List<Class<?>> modelTypes;
    public List<Class<?>> predicateTypes;
    public List<Class<?>> fontTypes;
    public List<Class<?>> propertyTypes;
    public List<Class<?>> propertyValueTypes;
    private boolean initialized = false;

    public DashLoaderAPI() {
        modelMappings = Collections.synchronizedMap(new HashMap<>());
        propertyMappings = Collections.synchronizedMap(new HashMap<>());
        propertyValueMappings = Collections.synchronizedMap(new HashMap<>());
        predicateMappings = Collections.synchronizedMap(new HashMap<>());
        fontMappings = Collections.synchronizedMap(new HashMap<>());
        modelTypes = new ArrayList<>();
        predicateTypes = new ArrayList<>();
        fontTypes = new ArrayList<>();
        propertyTypes = new ArrayList<>();
        propertyValueTypes = new ArrayList<>();
    }

    public static FactoryConstructor createConstructor(Class<?> dashClass, Class<?> rawClass) throws NoSuchMethodException, IllegalAccessException {
        final Constructor<?>[] constructors = dashClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(DashConstructor.class)) {
                final DashConstructor[] dashConstructors = constructor.getAnnotationsByType(DashConstructor.class);
                if (dashConstructors.length != 0) {
                    final ConstructorMode value = dashConstructors[0].value();
                    try {
                        return FactoryConstructor.createConstructor(value, dashClass, rawClass);
                    } catch (NoSuchMethodException e) {
                        throw new NoSuchMethodException(value.getExpectedMethod(dashClass, rawClass));
                    }
                }
            }
        }
        try {
            return FactoryConstructor.createConstructor(ConstructorMode.DEFAULT_PARAMETERS, dashClass, rawClass);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException(ConstructorMode.DEFAULT_PARAMETERS.getExpectedMethod(dashClass, rawClass));
        }
    }

    private void clearAPI() {
        modelMappings.clear();
        propertyMappings.clear();
        propertyValueMappings.clear();
        fontMappings.clear();
        predicateMappings.clear();
        modelTypes.clear();
        predicateTypes.clear();
        fontTypes.clear();
        propertyTypes.clear();
        propertyValueTypes.clear();
    }

    @SuppressWarnings("unchecked")
    private void addType(FactoryType type, Class<?> dashClass, Class<?> targetClass, FactoryConstructor constructor) {
        switch (type) {
            case PROPERTY_VALUE -> {
                propertyValueTypes.add(dashClass);
                propertyValueMappings.put((Class<? extends Comparable<?>>) targetClass, constructor);
            }
            case PROPERTY -> {
                propertyTypes.add(dashClass);
                propertyMappings.put((Class<? extends Property<?>>) targetClass, constructor);
            }
            case MODEL -> {
                modelTypes.add(dashClass);
                modelMappings.put((Class<? extends BakedModel>) targetClass, constructor);
            }
            case FONT -> {
                fontTypes.add(dashClass);
                fontMappings.put((Class<? extends Font>) targetClass, constructor);
            }
            case PREDICATE -> {
                predicateTypes.add(dashClass);
                predicateMappings.put((Class<? extends MultipartModelSelector>) targetClass, constructor);
            }
        }
        LOGGER.info("Added custom DashObject: {} {}", type, dashClass.getSimpleName());
    }

    private FactoryType getTypeFromFactoryInterface(Class<?> closs) {
        for (FactoryType value : FactoryType.values()) {
            if (value.factoryInterface == closs) {
                return value;
            }
        }
        LOGGER.error("Cannot find Factory Type from {} class parameter.", closs.getSimpleName());
        return null;
    }

    public void registerDashObject(Class<?> closs) {
        final Class<?>[] interfaces = closs.getInterfaces();
        if (interfaces.length == 0) {
            LOGGER.error("No Interfaces found. Class: {}", closs.getSimpleName());
            return;
        }
        final DashObject annotation = closs.getDeclaredAnnotation(DashObject.class);
        if (annotation == null) {
            LOGGER.error("Custom DashObject implementation does not have DashObject Annotation. Class: {}", closs.getSimpleName());
            return;
        }
        FactoryType type = annotation.overrideType();
        if (type == FactoryType.DEFAULT) {
            type = getTypeFromFactoryInterface(interfaces[0]);
        }
        if (type == null) {
            LOGGER.error("Factory type could not be identified. Class: {}", closs.getSimpleName());
            return;
        }
        final Class<?> rawClass = annotation.value();
        try {
            addType(type, closs, rawClass, createConstructor(closs, rawClass));
        } catch (NoSuchMethodException e) {
            LOGGER.error("Constructor not matching/found. Expected: {}", e.getMessage());
        } catch (IllegalAccessException e) {
            LOGGER.error("Constructor not accessible in {}", closs.getSimpleName());
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
        predicateTypes.add(DashStaticPredicate.class); // still cursed

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
            FabricLoader.getInstance().getAllMods().parallelStream().forEach(modContainer -> {
                final ModMetadata metadata = modContainer.getMetadata();
                processModData(metadata.getCustomValue("dashloader:customobject"), metadata);
            });
            sortTypes();
            LOGGER.info("[" + Duration.between(start, Instant.now()).toMillis() + "ms] Initialized api.");
            initialized = true;
        }
    }

    private void sortTypes() {
        modelTypes.sort(Comparator.comparing(Class::getSimpleName));
        predicateTypes.sort(Comparator.comparing(Class::getSimpleName));
        fontTypes.sort(Comparator.comparing(Class::getSimpleName));
        propertyTypes.sort(Comparator.comparing(Class::getSimpleName));
        propertyValueTypes.sort(Comparator.comparing(Class::getSimpleName));
    }

    private void processModData(CustomValue value, ModMetadata modMetadata) {
        if (value != null) {
            value.getAsArray().forEach(object -> {
                final String dashObject = object.getAsString();
                try {
                    final Class<?> aClass = Class.forName(dashObject);
                    registerDashObject(aClass);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Custom Dashable Object not found in mod {}. Value: {}", modMetadata.getId(), object);
                }
            });
        }
    }


}