package net.quantumfusion.dashloader.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.api.font.BitmapFontFactory;
import net.quantumfusion.dashloader.api.font.BlankFontFactory;
import net.quantumfusion.dashloader.api.font.FontFactory;
import net.quantumfusion.dashloader.api.font.UnicodeFontFactory;
import net.quantumfusion.dashloader.api.model.*;
import net.quantumfusion.dashloader.api.predicate.AndPredicateFactory;
import net.quantumfusion.dashloader.api.predicate.OrPredicateFactory;
import net.quantumfusion.dashloader.api.predicate.PredicateFactory;
import net.quantumfusion.dashloader.api.predicate.SimplePredicateFactory;
import net.quantumfusion.dashloader.api.property.*;
import net.quantumfusion.dashloader.model.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.util.ClassHelper;
import net.quantumfusion.dashloader.util.ThreadHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DashLoaderAPI {
    public static final Logger LOGGER = LogManager.getLogger();
    public final Map<Class<? extends BakedModel>, ModelFactory> modelMappings;
    public final Map<Class<? extends Property>, PropertyFactory> propertyMappings;
    public final Map<Class<? extends Font>, FontFactory> fontMappings;
    public final Map<Class<? extends MultipartModelSelector>, PredicateFactory> predicateMappings;

    public List<Class<?>> modelTypes;
    public List<Class<?>> predicateTypes;
    public List<Class<?>> fontTypes;
    public List<Class<?>> propertyTypes;
    public List<Class<?>> propertyValueTypes;


    private boolean initialized = false;

    public DashLoaderAPI() {
        modelMappings = Collections.synchronizedMap(new HashMap<>());
        propertyMappings = Collections.synchronizedMap(new HashMap<>());
        predicateMappings = Collections.synchronizedMap(new HashMap<>());
        fontMappings = Collections.synchronizedMap(new HashMap<>());
        predicateTypes = new ArrayList<>();
    }

    private void addModelType(ModelFactory factory) {
        modelMappings.put(factory.getType(), factory);
    }

    private void addPropertyType(PropertyFactory factory) {
        propertyMappings.put(factory.getType(), factory);
    }

    private void addFontType(FontFactory factory) {
        fontMappings.put(factory.getType(), factory);
    }

    private void addPredicateType(PredicateFactory factory) {
        predicateMappings.put(factory.getType(), factory);
    }

    private void clearAPI() {
        modelMappings.clear();
        propertyMappings.clear();
        fontMappings.clear();
        predicateMappings.clear();
        predicateTypes.clear();
    }

    private void initNativeAPI() {
        ThreadHelper.exec(() -> {
            addPropertyType(new BooleanPropertyFactory());
            addPropertyType(new IntPropertyFactory());
            addPropertyType(new EnumPropertyFactory());
            addPropertyType(new DirectionPropertyFactory());
        }, () -> {
            addModelType(new BasicBakedModelFactory());
            addModelType(new BuiltInBakedModelFactory());
            addModelType(new MultipartBakedModelFactory());
            addModelType(new WeightedBakedModelFactory());
        }, () -> {
            addPredicateType(new AndPredicateFactory());
            addPredicateType(new OrPredicateFactory());
            addPredicateType(new SimplePredicateFactory());
            predicateTypes.add(DashStaticPredicate.class); // cursed
        }, () -> {
            addFontType(new BitmapFontFactory());
            addFontType(new BlankFontFactory());
            addFontType(new UnicodeFontFactory());
        });
    }

    private void initTypes() {
        ThreadHelper.exec(
                () -> modelTypes = getDashTypes(modelMappings),
                () -> predicateTypes.addAll(getDashTypes(predicateMappings)),
                () -> fontTypes = getDashTypes(fontMappings),
                () -> {
                    propertyTypes = getDashTypes(propertyMappings);
                    propertyValueTypes = propertyMappings.values().stream().map(PropertyFactory::getDashValueType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList());
                }
        );
    }

    private <T, D> List<Class<?>> getDashTypes(Map<Class<? extends T>, ? extends Factory<T, D>> factory) {
        return factory.values().stream().map(Factory::getDashType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList());
    }

    public void initAPI() {
        if (!initialized) {
            Instant start = Instant.now();
            clearAPI();
            initNativeAPI();
            FabricLoader.getInstance().getAllMods().parallelStream().forEach(modContainer -> {
                final ModMetadata metadata = modContainer.getMetadata();
                getFactoryValue(metadata.getCustomValue("dashloader:customfactory"), metadata);
            });
            initTypes();
            LOGGER.info("[" + Duration.between(start, Instant.now()).toMillis() + "ms] Initialized api.");
            initialized = true;
        }
    }


    private void getFactoryValue(CustomValue values, ModMetadata modMetadata) {
        if (values != null) {
            for (CustomValue value : values.getAsArray()) {
                final Class<?> cls = ClassHelper.forName(value.getAsString());
                if (cls != null) {
                    final Factory<?, ?> factory = (Factory<?, ?>) Unsafe.allocateInstance(cls);
                    switch (factory.getFactoryType()) {
                        case MODEL -> {
                            final ModelFactory modelProxy = (ModelFactory) factory;
                            addModelType(modelProxy);
                        }
                        case PREDICATE -> {
                            final PredicateFactory predicateProxy = (PredicateFactory) factory;
                            addPredicateType(predicateProxy);
                        }
                        case FONT -> {
                            final FontFactory fontProxy = (FontFactory) factory;
                            addFontType(fontProxy);
                        }
                        case PROPERTY -> {
                            final PropertyFactory propertyFactory = (PropertyFactory) factory;
                            addPropertyType(propertyFactory);
                        }
                        case DEFAULT -> {
                            LOGGER.warn("Proxy Type not set" + value.getAsString());
                            continue;
                        }
                        default -> {
                            LOGGER.warn("Proxy Type unknown." + value.getAsString());
                            continue;
                        }
                    }
                    LOGGER.info("Added custom " + factory.getFactoryType().name + ": " + factory.getType().getSimpleName());
                } else {
                    LOGGER.warn("Factory not found in mod: " + modMetadata.getName() + " with value: \"" + values.getAsString() + "\"");
                }
            }
        }
    }

}