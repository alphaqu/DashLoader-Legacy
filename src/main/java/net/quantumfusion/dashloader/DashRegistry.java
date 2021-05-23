package net.quantumfusion.dashloader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.api.predicates.PredicateFactory;
import net.quantumfusion.dashloader.api.properties.PropertyFactory;
import net.quantumfusion.dashloader.atlas.DashImage;
import net.quantumfusion.dashloader.atlas.DashSprite;
import net.quantumfusion.dashloader.blockstates.DashBlockState;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;
import net.quantumfusion.dashloader.common.DashID;
import net.quantumfusion.dashloader.common.DashIdentifier;
import net.quantumfusion.dashloader.font.fonts.DashFont;
import net.quantumfusion.dashloader.mixin.NativeImageAccessor;
import net.quantumfusion.dashloader.models.DashModel;
import net.quantumfusion.dashloader.models.DashModelIdentifier;
import net.quantumfusion.dashloader.models.predicates.DashPredicate;
import net.quantumfusion.dashloader.models.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.registry.*;
import net.quantumfusion.dashloader.util.ThreadHelper;
import net.quantumfusion.dashloader.util.UndashTask;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DashRegistry {


    private static int totalTasks = 6;
    private static int tasksDone = 0;
    private final Map<Long, DashModel> models;
    public Map<Class, FactoryType> apiFailed = new ConcurrentHashMap<>();
    public Map<Long, BlockState> blockstatesOut;
    public Map<Long, Predicate<BlockState>> predicateOut;
    public Map<Long, Identifier> identifiersOut;
    public Map<Long, BakedModel> modelsOut;
    public Map<Long, Sprite> spritesOut;
    public Map<Long, Font> fontsOut;
    public Map<Long, NativeImage> imagesOut;
    public Map<Long, Property<?>> propertiesOut;
    public Map<Long, Comparable<?>> propertyValuesOut;
    DashLoader loader;
    private Map<Long, DashBlockState> blockstates;
    private Map<Long, DashSprite> sprites;
    private Map<Long, DashID> identifiers;
    private Map<Long, DashFont> fonts;
    private Map<Long, DashImage> images;
    private Map<Long, DashPredicate> predicates;

    private Map<Long, DashProperty> properties;
    private Map<Long, DashPropertyValue> propertyValues;

    private List<Map<Long, DashModel>> modelsToDeserialize;


    public DashRegistry(Map<Long, DashBlockState> blockstates,
                        Map<Long, DashSprite> sprites,
                        Map<Long, DashID> identifiers,
                        Map<Long, DashModel> models,
                        Map<Long, DashFont> fonts,
                        Map<Long, DashImage> images,
                        Map<Long, DashPredicate> predicates,
                        Map<Long, DashProperty> properties,
                        Map<Long, DashPropertyValue> propertyValues) {
        this.blockstates = blockstates;
        this.sprites = sprites;
        this.identifiers = identifiers;
        this.models = models;
        this.fonts = fonts;
        this.images = images;
        this.predicates = predicates;
        this.properties = properties;
        this.propertyValues = propertyValues;
    }

    public DashRegistry(DashLoader loader) {
        blockstates = new HashMap<>();
        sprites = new HashMap<>();
        identifiers = new HashMap<>();
        models = new HashMap<>();
        fonts = new HashMap<>();
        predicates = new HashMap<>();
        images = new HashMap<>();
        properties = new HashMap<>();
        propertyValues = new HashMap<>();
        modelsToDeserialize = new ArrayList<>();
        this.loader = loader;
    }

    public RegistryBlockStateData getBlockstates() {
        return new RegistryBlockStateData(blockstates);
    }

    public void setBlockstates(Map<Long, DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }

    public RegistrySpriteData getSprites() {
        return new RegistrySpriteData(sprites);
    }

    public void setSprites(Map<Long, DashSprite> sprites) {
        this.sprites = sprites;
    }

    public RegistryIdentifierData getIdentifiers() {
        return new RegistryIdentifierData(identifiers);
    }

    public void setIdentifiers(Map<Long, DashID> identifiers) {
        this.identifiers = identifiers;
    }

    public RegistryModelData getModels() {
        HashMap<Integer, Map<Long, DashModel>> modelsToAdd = new HashMap<>();
        models.forEach((aLong, dashModel) -> {

            final Map<Long, DashModel> longDashModelMap = modelsToAdd.get(dashModel.getStage());
            if (longDashModelMap == null) {
                final HashMap<Long, DashModel> element = new HashMap<>();
                element.put(aLong, dashModel);
                modelsToAdd.put(dashModel.getStage(), element);
            } else {
                longDashModelMap.put(aLong, dashModel);
            }
        });
        modelsToDeserialize = new ArrayList<>();
        modelsToAdd.forEach(modelsToDeserialize::add);
        return new RegistryModelData(modelsToDeserialize);
    }

    public void setModels(RegistryModelData data) {
        modelsToDeserialize = data.models;
    }

    public RegistryFontData getFonts() {
        return new RegistryFontData(fonts);
    }

    public void setFonts(Map<Long, DashFont> fonts) {
        this.fonts = fonts;
    }

    public RegistryImageData getImages() {
        return new RegistryImageData(images);
    }

    public void setImages(Map<Long, DashImage> images) {
        this.images = images;
    }

    public RegistryPredicateData getPredicates() {
        return new RegistryPredicateData(predicates);
    }

    public void setPredicates(Map<Long, DashPredicate> predicates) {
        this.predicates = predicates;
    }

    public RegistryPropertyData getProperties() {
        return new RegistryPropertyData(properties);
    }

    public void setProperties(Map<Long, DashProperty> properties) {
        this.properties = properties;
    }

    public RegistryPropertyValueData getPropertyValues() {
        return new RegistryPropertyValueData(propertyValues);
    }

    public void setPropertyValues(Map<Long, DashPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public long createBlockStatePointer(BlockState blockState) {
        final long hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState, this));
        }
        return hash;
    }

    public final <K> Long createModelPointer(final BakedModel bakedModel, @Nullable K var) {
        if (bakedModel == null) {
            return null;
        }
        final long hash = bakedModel.hashCode();
        if (models.get(hash) == null) {
            Factory<BakedModel, DashModel> model = loader.getApi().modelMappings.get(bakedModel.getClass());
            if (model != null) {
                models.put(hash, model.toDash(bakedModel, this, var));
            } else {
                apiFailed.putIfAbsent(bakedModel.getClass(), FactoryType.MODEL);
            }
        }
        return hash;
    }

    public final long createSpritePointer(final Sprite sprite) {
        final long hash = sprite.hashCode();
        if (sprites.get(hash) == null) {
            sprites.put(hash, new DashSprite(sprite, this));
        }
        return hash;
    }

    public final long createIdentifierPointer(final Identifier identifier) {
        final long hash = identifier.hashCode();
        if (identifiers.get(hash) == null) {
            if (identifier instanceof ModelIdentifier) {
                identifiers.put(hash, new DashModelIdentifier((ModelIdentifier) identifier));
            } else {
                identifiers.put(hash, new DashIdentifier(identifier));
            }
        }
        return hash;
    }

    public final long createImagePointer(final NativeImage image) {
        final long hash = ((NativeImageAccessor) (Object) image).getPointer();
        if (images.get(hash) == null) {
            images.put(hash, new DashImage(image));
        }
        return hash;
    }

    public final long createPredicatePointer(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final long hash = selector.hashCode();
        if (predicates.get(hash) == null) {
            predicates.put(hash, obtainPredicate(selector, stateManager));
        }
        return hash;
    }

    public final DashPredicate obtainPredicate(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final boolean isTrue = selector == MultipartModelSelector.TRUE;
        if (selector == MultipartModelSelector.FALSE || isTrue) {
            return new DashStaticPredicate(isTrue);
        } else {
            PredicateFactory predicateFactory = loader.getApi().predicateMappings.get(selector.getClass());
            if (predicateFactory != null) {
                return predicateFactory.toDash(selector, this, stateManager);
            } else {
                apiFailed.putIfAbsent(selector.getClass(), FactoryType.PREDICATE);
            }
        }
        return null;
    }


    public final long createFontPointer(final Font font) {
        final long hash = font.hashCode();
        if (fonts.get(hash) == null) {
            Factory<Font, DashFont> fontFactory = loader.getApi().fontMappings.get(font.getClass());
            if (fontFactory != null) {
                fonts.put(hash, fontFactory.toDash(font, this, null));
            } else {
                apiFailed.putIfAbsent(font.getClass(), FactoryType.FONT);
            }
        }
        return hash;
    }

    public final Pair<Long, Long> createPropertyPointer(final Property<?> property, final Comparable<?> value) {
        final long hashV = value.hashCode();
        final long hashP = property.hashCode();
        final boolean propVal = !propertyValues.containsKey(hashV);
        final boolean prop = !properties.containsKey(hashP);
        if (propVal || prop) {
            PropertyFactory propertyFactory = loader.getApi().propertyMappings.get(property.getClass());
            if (propertyFactory != null) {
                if (propVal) {
                    propertyValues.put(hashV, propertyFactory.toDash(value, this, hashP));
                }
                if (prop) {
                    properties.put(hashP, propertyFactory.toDash(property, this, hashP));
                }
            } else {
                apiFailed.put(property.getClass(), FactoryType.PROPERTY);
            }
        }
        return Pair.of(hashP, hashV);
    }

    public final BlockState getBlockstate(final Long pointer) {
        final BlockState blockstate = blockstatesOut.get(pointer);
        if (blockstate == null) {
            DashLoader.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public final Sprite getSprite(final Long pointer) {
        final Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            DashLoader.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public final Identifier getIdentifier(final Long pointer) {
        final Identifier identifier = identifiersOut.get(pointer);
        if (identifier == null) {
            DashLoader.LOGGER.error("Identifier not found in data. PINTR: " + pointer);
        }
        return identifier;
    }

    public final BakedModel getModel(final Long pointer) {
        final BakedModel bakedModel = modelsOut.get(pointer);
        if (bakedModel == null) {
            DashLoader.LOGGER.error("Model not found in data. PINTR: " + pointer);
        }
        return bakedModel;
    }

    public final Font getFont(final Long pointer) {
        final Font font = fontsOut.get(pointer);
        if (font == null) {
            DashLoader.LOGGER.error("Font not found in data. PINTR: " + pointer);
        }
        return font;
    }

    public final NativeImage getImage(final Long pointer) {
        final NativeImage image = imagesOut.get(pointer);
        if (image == null) {
            DashLoader.LOGGER.error("NativeImage not found in data. PINTR: " + pointer);
        }
        return image;
    }

    public final Predicate<BlockState> getPredicate(final Long pointer) {
        final Predicate<BlockState> predicate = predicateOut.get(pointer);
        if (predicate == null) {
            DashLoader.LOGGER.error("Predicate not found in data. PINTR: " + pointer);
        }
        return predicate;
    }

    public final Pair<Property<?>, Comparable<?>> getProperty(final Long propertyPointer, final Long valuePointer) {
        final Property<?> property = propertiesOut.get(propertyPointer);
        final Comparable<?> value = propertyValuesOut.get(valuePointer);
        if (property == null || value == null) {
            DashLoader.LOGGER.error("Property not found in data. PINTR: " + propertyPointer + "/" + valuePointer);
        }
        return Pair.of(property, value);
    }

    public void toUndash() {
        Logger logger = LogManager.getLogger();
        totalTasks = 4 + modelsToDeserialize.size();
        log(logger, "Loading Simple Objects");
        identifiersOut = ThreadHelper.execParallel(identifiers, this);
        imagesOut = ThreadHelper.execParallel(images, this);
        identifiers = null;
        images = null;

        log(logger, "Loading Properties");
        propertiesOut = ThreadHelper.execParallel(properties, this);
        propertyValuesOut = ThreadHelper.execParallel(propertyValues, this);
        properties = null;
        propertyValues = null;

        log(logger, "Loading Advanced Objects");
        blockstatesOut = ThreadHelper.execParallel(blockstates, this);
        predicateOut = ThreadHelper.execParallel(predicates, this);
        spritesOut = ThreadHelper.execParallel(sprites, this);
        fontsOut = ThreadHelper.execParallel(fonts, this);
        blockstates = null;
        predicates = null;
        sprites = null;
        fonts = null;

        modelsOut = new ConcurrentHashMap<>(models.size());
        final short[] currentStage = {0};
        modelsToDeserialize.forEach(modelCategory -> {
            log(logger, "Loading " + modelCategory.size() + " Models: " + "[" + currentStage[0] + "]");
            modelsOut.putAll(ThreadHelper.execParallel(modelCategory, this));
            currentStage[0]++;
        });
        log(logger, "Applying Model Overrides");
        modelsToDeserialize.forEach(modelcategory -> DashLoader.THREADPOOL.invoke(new UndashTask.ApplyTask(new ArrayList<>(modelcategory.values()), 100, this)));
        modelsToDeserialize = null;
    }

    private void log(Logger logger, String s) {
        tasksDone++;
        logger.info("[" + tasksDone + "/" + totalTasks + "] " + s);
    }

    public void apiReport(Logger logger) {
        if (apiFailed.size() != 0) {
            logger.error("Found incompatible objects that were not able to be serialized.");
            int[] ints = new int[1];
            apiFailed.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().name)).forEach(entry -> {
                ints[0]++;
                logger.error("[" + entry.getValue().name() + "] Object: " + entry.getKey().getName());
            });
            logger.error("In total there are " + ints[0] + " incompatible objects. Please contact the mod developers to add support.");
        }
    }
}
