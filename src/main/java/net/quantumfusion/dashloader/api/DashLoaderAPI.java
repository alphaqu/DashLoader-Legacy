package net.quantumfusion.dashloader.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.api.fonts.BitmapFontFactory;
import net.quantumfusion.dashloader.api.fonts.BlankFontFactory;
import net.quantumfusion.dashloader.api.fonts.UnicodeFontFactory;
import net.quantumfusion.dashloader.api.models.BasicBakedModelFactory;
import net.quantumfusion.dashloader.api.models.BuiltInBakedModelFactory;
import net.quantumfusion.dashloader.api.models.MultipartBakedModelFactory;
import net.quantumfusion.dashloader.api.models.WeightedBakedModelFactory;
import net.quantumfusion.dashloader.api.predicates.AndPredicateFactory;
import net.quantumfusion.dashloader.api.predicates.OrPredicateFactory;
import net.quantumfusion.dashloader.api.predicates.SimplePredicateFactory;
import net.quantumfusion.dashloader.api.predicates.StaticPredicate;
import net.quantumfusion.dashloader.api.properties.*;
import net.quantumfusion.dashloader.font.fonts.DashFont;
import net.quantumfusion.dashloader.models.DashModel;
import net.quantumfusion.dashloader.models.predicates.DashPredicate;
import net.quantumfusion.dashloader.util.ThreadHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DashLoaderAPI {
    public static final Logger LOGGER = LogManager.getLogger();
    public final Map<Class<? extends BakedModel>, Factory<BakedModel, DashModel>> modelMappings;
    public final Map<Class<? extends Property>, PropertyFactory> propertyMappings;
    public final Map<Class<? extends Font>, Factory<Font, DashFont>> fontMappings;
    public final Map<Class<? extends MultipartModelSelector>, Factory<MultipartModelSelector, DashPredicate>> predicateMappings;
    public List<Class<?>> modelTypes;
    public List<Class<?>> predicateTypes;
    public List<Class<?>> fontTypes;

    public List<Class<?>> propertyTypes;
    public List<Class<?>> propertyValueTypes;

    public DashLoaderAPI() {
        modelMappings = new ConcurrentHashMap<>();
        propertyMappings = new ConcurrentHashMap<>();
        predicateMappings = new ConcurrentHashMap<>();
        fontMappings = new ConcurrentHashMap<>();
        modelTypes = new ArrayList<>();
        predicateTypes = new ArrayList<>();
        fontTypes = new ArrayList<>();
        propertyTypes = new ArrayList<>();
        propertyValueTypes = new ArrayList<>();
    }

    private void addModelType(Factory<BakedModel, DashModel> factory) {
        modelMappings.put(factory.getType(), factory);
    }

    private void addPropertyType(PropertyFactory factory) {
        propertyMappings.put(factory.getType(), factory);
    }

    private void addFontType(Factory<Font, DashFont> factory) {
        fontMappings.put(factory.getType(), factory);
    }

    private void addPredicateType(Factory<MultipartModelSelector, DashPredicate> factory) {
        predicateMappings.put(factory.getType(), factory);
    }

    private void clearAPI() {
        modelMappings.clear();
        propertyMappings.clear();
        fontMappings.clear();
        predicateMappings.clear();
        modelTypes.clear();
        predicateTypes.clear();
        fontTypes.clear();
        propertyTypes.clear();
        propertyValueTypes.clear();
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
            addPredicateType(new StaticPredicate());
        }, () -> {
            addFontType(new BitmapFontFactory());
            addFontType(new BlankFontFactory());
            addFontType(new UnicodeFontFactory());
        });
    }

    private void initTypes() {
        ThreadHelper.exec(
                () -> modelTypes.addAll(modelMappings.values().stream().map(Factory::getDashType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList())),
                () -> predicateTypes.addAll(predicateMappings.values().stream().map(Factory::getDashType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList())),
                () -> fontTypes.addAll(fontMappings.values().stream().map(Factory::getDashType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList())),
                () -> {
                    propertyTypes.addAll(propertyMappings.values().stream().map(Factory::getDashType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList()));
                    propertyValueTypes.addAll(propertyMappings.values().stream().map(PropertyFactory::getDashValueType).sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList()));
                }
        );
    }

    public void initAPI() {
        Instant start = Instant.now();
        clearAPI();
        initNativeAPI();
        FabricLoader.getInstance().getAllMods().parallelStream().forEach(modContainer -> {
            final ModMetadata metadata = modContainer.getMetadata();
            getValue(metadata.getCustomValue("dashloader:factory"), metadata);
        });
        initTypes();
        LOGGER.info("[" + Duration.between(start, Instant.now()).toMillis() + "ms] Initialized api.");
    }

    private void getValue(CustomValue values, ModMetadata modMetadata) {
        if (values != null) {
            try {
                for (CustomValue value : values.getAsArray()) {
                    final Factory<?, ?> factory = (Factory<?, ?>) Unsafe.allocateInstance(Class.forName(value.getAsString()));
                    switch (factory.getFactoryType()) {
                        case MODEL:
                            final Factory<BakedModel, DashModel> modelProxy = (Factory<BakedModel, DashModel>) factory;
                            addModelType(modelProxy);
                            break;
                        case PREDICATE:
                            final Factory<MultipartModelSelector, DashPredicate> predicateProxy = (Factory<MultipartModelSelector, DashPredicate>) factory;
                            addPredicateType(predicateProxy);
                            break;
                        case FONT:
                            final Factory<Font, DashFont> fontProxy = (Factory<Font, DashFont>) factory;
                            addFontType(fontProxy);
                            break;
                        case PROPERTY:
                            final PropertyFactory propertyFactory = (PropertyFactory) factory;
                            addPropertyType(propertyFactory);
                            break;
                        case DEFAULT:
                            LOGGER.warn("Proxy Type not set" + value.getAsString());
                            continue;
                        default:
                            LOGGER.warn("Proxy Type unknown." + value.getAsString());
                            continue;
                    }
                    LOGGER.info("Added custom " + factory.getFactoryType().name + ": " + factory.getType().getSimpleName());
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Factory not found in mod: " + modMetadata.getName() + " with value: \"" + values.getAsString() + "\"");
            }
        }
    }


}
