package net.quantumfusion.dashloader.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.blockstate.property.DashBooleanProperty;
import net.quantumfusion.dashloader.blockstate.property.DashDirectionProperty;
import net.quantumfusion.dashloader.blockstate.property.DashEnumProperty;
import net.quantumfusion.dashloader.blockstate.property.DashIntProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashBooleanValue;
import net.quantumfusion.dashloader.blockstate.property.value.DashDirectionValue;
import net.quantumfusion.dashloader.blockstate.property.value.DashEnumValue;
import net.quantumfusion.dashloader.blockstate.property.value.DashIntValue;
import net.quantumfusion.dashloader.font.DashBitmapFont;
import net.quantumfusion.dashloader.font.DashBlankFont;
import net.quantumfusion.dashloader.font.DashTrueTypeFont;
import net.quantumfusion.dashloader.font.DashUnicodeFont;
import net.quantumfusion.dashloader.model.DashBasicBakedModel;
import net.quantumfusion.dashloader.model.DashBuiltinBakedModel;
import net.quantumfusion.dashloader.model.DashMultipartBakedModel;
import net.quantumfusion.dashloader.model.DashWeightedBakedModel;
import net.quantumfusion.dashloader.model.predicates.DashAndPredicate;
import net.quantumfusion.dashloader.model.predicates.DashOrPredicate;
import net.quantumfusion.dashloader.model.predicates.DashSimplePredicate;
import net.quantumfusion.dashloader.model.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.util.ClassHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DashLoaderAPI {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Class<?>[] defaultParameters = new Class[]{DashRegistry.class};
    public final Map<Class<? extends BakedModel>, MethodHandle> modelMappings;
    public final Map<Class<? extends Property<?>>, MethodHandle> propertyMappings;
    public final Map<Class<? extends Comparable<?>>, MethodHandle> propertyValueMappings;
    public final Map<Class<? extends Font>, MethodHandle> fontMappings;
    public final Map<Class<? extends MultipartModelSelector>, MethodHandle> predicateMappings;
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
    private void addType(FactoryType type, Class<?> dashClass, Class<?> targetClass, MethodHandle constructor) {
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

    public static MethodHandle createConstructor(Class<?> dashClass, Class<?> rawClass, FactoryType type) throws NoSuchMethodException, IllegalAccessException {
        final Class<?>[] extraParameters = type.extraParameters;
        List<Class<?>> parameters = new ArrayList<>();
        parameters.add(rawClass);
        parameters.addAll(Arrays.stream(defaultParameters).toList());
        parameters.addAll(Arrays.stream(extraParameters).toList());
        return MethodHandles.publicLookup().findConstructor(dashClass, MethodType.methodType(void.class, parameters));
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
        final FactoryType type;
        final FactoryType factoryType = annotation.overrideType();
        if (factoryType == FactoryType.DEFAULT) {
            type = getTypeFromFactoryInterface(interfaces[0]);
        } else {
            type = factoryType;
        }
        if (type == null) {
            LOGGER.error("Factory type could not be identified. Class: {}", closs.getSimpleName());
            return;
        }
        for (Class<?> rawClass : annotation.value()) {
            try {
                addType(type, closs, rawClass, createConstructor(closs, rawClass, type));
            } catch (NoSuchMethodException e) {
                StringBuilder expectedMethod = new StringBuilder();
                expectedMethod.append("public ");
                expectedMethod.append(closs.getSimpleName());
                expectedMethod.append('(');
                expectedMethod.append(thisAndCamelCase(rawClass.getSimpleName()));
                expectedMethod.append(thisAndCamelCase(ClassHelper.printArray(defaultParameters)));
                expectedMethod.append(thisAndCamelCase(ClassHelper.printArray(type.extraParameters)));
                expectedMethod.deleteCharAt(expectedMethod.length() - 1);
                expectedMethod.deleteCharAt(expectedMethod.length() - 1);
                expectedMethod.append(')');

                LOGGER.error("Creation Constructor not found in {}, Expected constructor signature -> {}",
                        closs.getSimpleName(),
                        expectedMethod.toString(), e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Constructor not accessible in {}", closs.getSimpleName());
            }
        }
    }

    private String thisAndCamelCase(String string) {
        return string + ' ' + (Character.toLowerCase(string.charAt(0)) + string.substring(1)) + ", ";
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