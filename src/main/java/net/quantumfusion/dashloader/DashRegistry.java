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
import net.quantumfusion.dashloader.api.predicate.PredicateFactory;
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
import java.util.function.Predicate;

public class DashRegistry {


    private static int totalTasks = 6;
    private static int tasksDone = 0;
    private final Map<Integer, DashModel> models;
    public Map<Class, FactoryType> apiFailed = new ConcurrentHashMap<>();
    public Map<Integer, BlockState> blockstatesOut;
    public Map<Integer, Predicate<BlockState>> predicateOut;
    public Map<Integer, Identifier> identifiersOut;
    public Map<Integer, BakedModel> modelsOut;
    public Map<Integer, Sprite> spritesOut;
    public Map<Integer, Font> fontsOut;
    public Map<Integer, NativeImage> imagesOut;
    public Map<Integer, Property<?>> propertiesOut;
    public Map<Integer, Comparable<?>> propertyValuesOut;
    DashLoader loader;
    private Map<Integer, DashBlockState> blockstates;
    private Map<Integer, DashSprite> sprites;
    private Map<Integer, DashID> identifiers;
    private Map<Integer, DashFont> fonts;
    private Map<Integer, DashImage> images;
    private Map<Integer, DashPredicate> predicates;

    private Map<Integer, DashProperty> properties;
    private Map<Integer, DashPropertyValue> propertyValues;

    private List<Map<Integer, DashModel>> modelsToDeserialize;


    public DashRegistry(Map<Integer, DashBlockState> blockstates,
                        Map<Integer, DashSprite> sprites,
                        Map<Integer, DashID> identifiers,
                        Map<Integer, DashModel> models,
                        Map<Integer, DashFont> fonts,
                        Map<Integer, DashImage> images,
                        Map<Integer, DashPredicate> predicates,
                        Map<Integer, DashProperty> properties,
                        Map<Integer, DashPropertyValue> propertyValues) {
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

    public void setBlockstates(Map<Integer, DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }

    public RegistrySpriteData getSprites() {
        return new RegistrySpriteData(sprites);
    }

    public void setSprites(Map<Integer, DashSprite> sprites) {
        this.sprites = sprites;
    }

    public RegistryIdentifierData getIdentifiers() {
        return new RegistryIdentifierData(identifiers);
    }

    public void setIdentifiers(Map<Integer, DashID> identifiers) {
        this.identifiers = identifiers;
    }

    public RegistryModelData getModels() {
        HashMap<Integer, Map<Integer, DashModel>> modelsToAdd = new HashMap<>();
        models.forEach((aInteger, dashModel) -> {

            final Map<Integer, DashModel> IntegerDashModelMap = modelsToAdd.get(dashModel.getStage());
            if (IntegerDashModelMap == null) {
                final HashMap<Integer, DashModel> element = new HashMap<>();
                element.put(aInteger, dashModel);
                modelsToAdd.put(dashModel.getStage(), element);
            } else {
                IntegerDashModelMap.put(aInteger, dashModel);
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

    public void setFonts(Map<Integer, DashFont> fonts) {
        this.fonts = fonts;
    }

    public RegistryImageData getImages() {
        return new RegistryImageData(images);
    }

    public void setImages(Map<Integer, DashImage> images) {
        this.images = images;
    }

    public RegistryPredicateData getPredicates() {
        return new RegistryPredicateData(predicates);
    }

    public void setPredicates(Map<Integer, DashPredicate> predicates) {
        this.predicates = predicates;
    }

    public RegistryPropertyData getProperties() {
        return new RegistryPropertyData(properties);
    }

    public void setProperties(Map<Integer, DashProperty> properties) {
        this.properties = properties;
    }

    public RegistryPropertyValueData getPropertyValues() {
        return new RegistryPropertyValueData(propertyValues);
    }

    public void setPropertyValues(Map<Integer, DashPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public Integer createBlockStatePointer(BlockState blockState) {
        final Integer hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState, this));
        }
        return hash;
    }

    public final Integer createModelPointer(final BakedModel bakedModel) {
        if (bakedModel == null) {
            return null;
        }
        final Integer hash = bakedModel.hashCode();
        if (models.get(hash) == null) {
            Factory<BakedModel, DashModel> model = loader.getApi().modelMappings.get(bakedModel.getClass());
            if (model != null) {
                models.put(hash, model.toDash(bakedModel, this, DashLoader.getInstance().multipartData.get(bakedModel)));
            } else {
                apiFailed.putIfAbsent(bakedModel.getClass(), FactoryType.MODEL);
            }
        }
        return hash;
    }

    public final Integer createSpritePointer(final Sprite sprite) {
        final Integer hash = sprite.hashCode();
        if (sprites.get(hash) == null) {
            sprites.put(hash, new DashSprite(sprite, this));
        }
        return hash;
    }

    public final Integer createIdentifierPointer(final Identifier identifier) {
        final Integer hash = identifier.hashCode();
        if (identifiers.get(hash) == null) {
            if (identifier instanceof ModelIdentifier) {
                identifiers.put(hash, new DashModelIdentifier((ModelIdentifier) identifier));
            } else {
                identifiers.put(hash, new DashIdentifier(identifier));
            }
        }
        return hash;
    }

    public final Integer createImagePointer(final NativeImage image) {
        final Integer hash = image.hashCode();
        if (images.get(hash) == null) {
            images.put(hash, new DashImage(image));
        }
        return hash;
    }

    public final Integer createPredicatePointer(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final Integer hash = selector.hashCode();
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


    public final Integer createFontPointer(final Font font) {
        final Integer hash = font.hashCode();
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

    public final Pair<Integer, Integer> createPropertyPointer(final Property<?> property, final Comparable<?> value) {
        final Integer hashV = value.hashCode();
        final Integer hashP = property.hashCode();
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

    public final BlockState getBlockstate(final Integer pointer) {
        final BlockState blockstate = blockstatesOut.get(pointer);
        if (blockstate == null) {
            DashLoader.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public final Sprite getSprite(final Integer pointer) {
        final Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            DashLoader.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public final Identifier getIdentifier(final Integer pointer) {
        final Identifier identifier = identifiersOut.get(pointer);
        if (identifier == null) {
            DashLoader.LOGGER.error("Identifier not found in data. PINTR: " + pointer);
        }
        return identifier;
    }

    public final BakedModel getModel(final Integer pointer) {
        final BakedModel bakedModel = modelsOut.get(pointer);
        if (bakedModel == null) {
            DashLoader.LOGGER.error("Model not found in data. PINTR: " + pointer);
        }
        return bakedModel;
    }

    public final Font getFont(final Integer pointer) {
        final Font font = fontsOut.get(pointer);
        if (font == null) {
            DashLoader.LOGGER.error("Font not found in data. PINTR: " + pointer);
        }
        return font;
    }

    public final NativeImage getImage(final Integer pointer) {
        final NativeImage image = imagesOut.get(pointer);
        if (image == null) {
            DashLoader.LOGGER.error("NativeImage not found in data. PINTR: " + pointer);
        }
        return image;
    }

    public final Predicate<BlockState> getPredicate(final Integer pointer) {
        final Predicate<BlockState> predicate = predicateOut.get(pointer);
        if (predicate == null) {
            DashLoader.LOGGER.error("Predicate not found in data. PINTR: " + pointer);
        }
        return predicate;
    }

    public final Pair<Property<?>, Comparable<?>> getProperty(final Integer propertyPointer, final Integer valuePointer) {
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

        modelsOut = Collections.synchronizedMap(new HashMap<>((int) Math.ceil(modelsToDeserialize.size() / 0.75)));
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
