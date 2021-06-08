package net.quantumfusion.dashloader;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.api.property.PropertyFactory;
import net.quantumfusion.dashloader.blockstate.DashBlockState;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;
import net.quantumfusion.dashloader.data.DashID;
import net.quantumfusion.dashloader.data.DashIdentifier;
import net.quantumfusion.dashloader.data.registry.*;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.image.DashImage;
import net.quantumfusion.dashloader.image.DashSprite;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.DashModelIdentifier;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;
import net.quantumfusion.dashloader.model.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.util.ThreadHelper;
import net.quantumfusion.dashloader.util.UndashTask;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class DashRegistry {
    private static int totalTasks = 6;
    private static int tasksDone = 0;
    public Int2ObjectSortedMap<DashModel> models;
    public Map<Class<?>, FactoryType> apiFailed = new ConcurrentHashMap<>();

    public Int2ObjectSortedMap<BlockState> blockStatesOut;
    public Int2ObjectSortedMap<Predicate<BlockState>> predicatesOut;
    public Int2ObjectSortedMap<Identifier> identifiersOut;
    public Int2ObjectSortedMap<BakedModel> modelsOut;
    public Int2ObjectSortedMap<Sprite> spritesOut;
    public Int2ObjectSortedMap<Font> fontsOut;
    public Int2ObjectSortedMap<NativeImage> imagesOut;
    public Int2ObjectSortedMap<Property<?>> propertiesOut;
    public Int2ObjectSortedMap<Comparable<?>> propertyValuesOut;

    public Int2ObjectSortedMap<DashBlockState> blockStates;
    public Int2ObjectSortedMap<DashSprite> sprites;
    public Int2ObjectSortedMap<DashID> identifiers;
    public Int2ObjectSortedMap<DashFont> fonts;
    public Int2ObjectSortedMap<DashImage> images;
    public Int2ObjectSortedMap<DashPredicate> predicates;
    public Int2ObjectSortedMap<DashProperty> properties;
    public Int2ObjectSortedMap<DashPropertyValue> propertyValues;
    public List<Int2ObjectSortedMap<DashModel>> modelsToDeserialize;

    private final DashLoader loader;

    public DashRegistry(DashLoader loader) {
        blockStates = new Int2ObjectLinkedOpenHashMap<>();
        sprites = new Int2ObjectLinkedOpenHashMap<>();
        identifiers = new Int2ObjectLinkedOpenHashMap<>();
        models = new Int2ObjectLinkedOpenHashMap<>();
        fonts = new Int2ObjectLinkedOpenHashMap<>();
        predicates = new Int2ObjectLinkedOpenHashMap<>();
        images = new Int2ObjectLinkedOpenHashMap<>();
        properties = new Int2ObjectLinkedOpenHashMap<>();
        propertyValues = new Int2ObjectLinkedOpenHashMap<>();
        modelsToDeserialize = new ArrayList<>();
        this.loader = loader;
    }

    public RegistryBlockStateData makeBlockStatesData() { return new RegistryBlockStateData(blockStates); }
    public RegistrySpriteData makeSpritesData() { return new RegistrySpriteData(sprites); }
    public RegistryIdentifierData makeIdentifiersData() { return new RegistryIdentifierData(identifiers); }
    public RegistryModelData makeModelsData() {
        final var modelsToAdd = new Int2ObjectLinkedOpenHashMap<Int2ObjectSortedMap<DashModel>>();

        for (final var entry : models.int2ObjectEntrySet()) {
            final var model = entry.getValue();
            final var modelMap = modelsToAdd.computeIfAbsent(model.getStage(), Int2ObjectLinkedOpenHashMap::new);
            modelMap.put(entry.getIntKey(), model);
        }
        modelsToDeserialize.clear();
        modelsToAdd.forEach(modelsToDeserialize::add);
        return new RegistryModelData(modelsToDeserialize);
    }
    public RegistryFontData makeFontsData() { return new RegistryFontData(fonts); }
    public RegistryImageData makeImagesData() { return new RegistryImageData(images); }
    public RegistryPredicateData makePredicatesData() { return new RegistryPredicateData(predicates); }
    public RegistryPropertyData makePropertiesData() { return new RegistryPropertyData(properties); }
    public RegistryPropertyValueData makePropertyValuesData() { return new RegistryPropertyValueData(propertyValues); }

    public int createBlockStatePointer(BlockState blockState) {
        final var hash = blockState.hashCode();
        blockStates.putIfAbsent(hash, new DashBlockState(blockState, this));
        return hash;
    }

    public int createModelPointer(final BakedModel bakedModel) {
        if (bakedModel == null) {
            return -1; // TODO: what should we do with this?
        }
        final int hash = bakedModel.hashCode();
        if (models.get(hash) == null) {
            final var model = loader.api.modelMappings.get(bakedModel.getClass());
            if (model != null && bakedModel instanceof MultipartBakedModel) {
                models.put(hash, model.toDash(bakedModel, this, DashLoader.getInstance().multipartData.get(bakedModel)));
            } else {
                apiFailed.putIfAbsent(bakedModel.getClass(), FactoryType.MODEL);
            }
        }
        return hash;
    }

    public int createSpritePointer(final Sprite sprite) {
        final var hash = sprite.hashCode();
        sprites.putIfAbsent(hash, new DashSprite(sprite, this));
        return hash;
    }

    public int createIdentifierPointer(final Identifier identifier) {
        final var hash = identifier.hashCode();
        identifiers.computeIfAbsent(hash, k -> {
            if (identifier instanceof ModelIdentifier modelIdentifier) {
                return new DashModelIdentifier(modelIdentifier);
            } else {
                return new DashIdentifier(identifier);
            }
        });
        return hash;
    }

    public int createImagePointer(final NativeImage image) {
        final var hash = image.hashCode();
        images.putIfAbsent(hash, new DashImage(image));
        return hash;
    }

    public int createPredicatePointer(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final var hash = selector.hashCode();
        predicates.computeIfAbsent(hash, k -> obtainPredicate(selector, stateManager));
        return hash;
    }

    public DashPredicate obtainPredicate(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final boolean isTrue = selector == MultipartModelSelector.TRUE;
        if (selector == MultipartModelSelector.FALSE || isTrue) {
            return new DashStaticPredicate(isTrue);
        } else {
            final var predicateFactory = loader.api.predicateMappings.get(selector.getClass());
            if (predicateFactory != null) {
                return predicateFactory.toDash(selector, this, stateManager);
            } else {
                apiFailed.putIfAbsent(selector.getClass(), FactoryType.PREDICATE);
            }
        }
        return null;
    }

    public int createFontPointer(final Font font) {
        final var hash = font.hashCode();
        if (fonts.get(hash) == null) {
            var fontFactory = loader.api.fontMappings.get(font.getClass());
            if (fontFactory != null) {
                fonts.put(hash, fontFactory.toDash(font, this, null));
            } else {
                apiFailed.putIfAbsent(font.getClass(), FactoryType.FONT);
            }
        }
        return hash;
    }

    // TODO: remind me to switch to IntIntPair when Minecraft updates fastutil
    public final Pair<Integer, Integer> createPropertyPointer(final Property<?> property, final Comparable<?> value) {
        final var hashVal = value.hashCode();
        final var hashProp = property.hashCode();
        final var hasPropVal = !propertyValues.containsKey(hashVal);
        final var hasProp = !properties.containsKey(hashProp);
        if (hasPropVal || hasProp) {
            final var propertyFactory = loader.api.propertyMappings.get(property.getClass());
            if (propertyFactory != null) {
                if (hasPropVal) {
                    propertyValues.put(hashVal, propertyFactory.toDash(value, this, hashProp));
                }
                if (hasProp) {
                    properties.put(hashProp, propertyFactory.toDash(property, this, hashProp));
                }
            } else {
                apiFailed.put(property.getClass(), FactoryType.PROPERTY);
            }
        }
        return Pair.of(hashProp, hashVal);
    }

    public BlockState getBlockState(final int pointer) { return logIfNullThenReturn(blockStatesOut, pointer, "BlockState"); }

    public Sprite getSprite(final int pointer) { return logIfNullThenReturn(spritesOut, pointer, "Sprite"); }

    public Identifier getIdentifier(final int pointer) { return logIfNullThenReturn(identifiersOut, pointer, "Identifier"); }

    public BakedModel getModel(final int pointer) { return logIfNullThenReturn(modelsOut, pointer, "BakedModel"); }

    public Font getFont(final int pointer) { return logIfNullThenReturn(fontsOut, pointer, "Font"); }

    public NativeImage getImage(final int pointer) { return logIfNullThenReturn(imagesOut, pointer, "NativeImage"); }

    public Predicate<BlockState> getPredicate(final int pointer) { return logIfNullThenReturn(predicatesOut, pointer, "Predicate"); }

    public Pair<Property<?>, Comparable<?>> getProperty(final int propertyPointer, final int valuePointer) {
        final Property<?> property = propertiesOut.get(propertyPointer);
        final Comparable<?> value = propertyValuesOut.get(valuePointer);
        if (property == null || value == null) {
            DashLoader.LOGGER.error("Property not found in data. PINTR: " + propertyPointer + "/" + valuePointer);
        }
        return Pair.of(property, value);
    }


    private <T> T logIfNullThenReturn(final Int2ObjectMap<T> map, final int ptr, final String typeStr) {
        final T t = map.get(ptr);
        if (t == null) {
            //reified type parameters when?  - leocth
            //DashLoader.LOGGER.error(T.class.getSimpleName() + " not found in data. PINTR: " + ptr);
            DashLoader.LOGGER.error(typeStr + " not found in data. PINTR: " + ptr);
        }
        return t;
    }

    public void toUndash() {
        Logger logger = LogManager.getLogger();
        totalTasks = 4 + modelsToDeserialize.size();
        log(logger, "Loading Simple Objects");
        identifiersOut = ThreadHelper.execParallel(identifiers, this);
        imagesOut = ThreadHelper.execParallel(images, this);
        identifiers.clear();
        images.clear();

        log(logger, "Loading Properties");
        propertiesOut = ThreadHelper.execParallel(properties, this);
        propertyValuesOut = ThreadHelper.execParallel(propertyValues, this);
        properties.clear();
        propertyValues.clear();

        log(logger, "Loading Advanced Objects");
        blockStatesOut = ThreadHelper.execParallel(blockStates, this);
        predicatesOut = ThreadHelper.execParallel(predicates, this);
        spritesOut = ThreadHelper.execParallel(sprites, this);
        fontsOut = ThreadHelper.execParallel(fonts, this);
        blockStates.clear();
        predicates.clear();
        sprites.clear();
        fonts.clear();

        modelsOut = Int2ObjectSortedMaps.synchronize(new Int2ObjectLinkedOpenHashMap<>((int) Math.ceil(modelsToDeserialize.size() / 0.75)));

        final var currentStage = new AtomicInteger();
        modelsToDeserialize.forEach(modelCategory -> {
            log(logger, "Loading {} Models: [{}]", modelCategory.size(), currentStage);
            modelsOut.putAll(ThreadHelper.execParallel(modelCategory, this));
            currentStage.getAndIncrement();
        });

        log(logger, "Applying Model Overrides");
        modelsToDeserialize.forEach(category -> DashLoader.THREAD_POOL.invoke(new UndashTask.ApplyTask(new ArrayList<>(category.values()), 100, this)));
        modelsToDeserialize.clear();
    }

    private void log(Logger logger, String s) {
        tasksDone++;
        logger.info("[{}/{}] {}", tasksDone, totalTasks, s);
    }

    private void log(Logger logger, String s, Object... params) {
        tasksDone++;
        final var formatString = "[{}/{}] " + s;
        logger.info(formatString, tasksDone, totalTasks, params);
    }

    public void apiReport(Logger logger) {
        if (apiFailed.size() != 0) {
            logger.error("Found incompatible objects that were not able to be serialized.");
            final var counter = new AtomicInteger();
            apiFailed.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().name)).forEach(entry -> {
                counter.getAndIncrement();
                logger.error("[{}] Object: {}", entry.getValue().name(), entry.getKey().getName());
            });
            logger.error("In total there are {} incompatible objects. Please contact the mod developers to add support.", counter.getAndIncrement());
        }
    }
}
