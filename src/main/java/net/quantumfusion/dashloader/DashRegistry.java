package net.quantumfusion.dashloader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.api.models.DashModelFactory;
import net.quantumfusion.dashloader.api.properties.DashPropertyFactory;
import net.quantumfusion.dashloader.atlas.DashImage;
import net.quantumfusion.dashloader.atlas.DashSprite;
import net.quantumfusion.dashloader.blockstates.DashBlockState;
import net.quantumfusion.dashloader.blockstates.properties.DashProperty;
import net.quantumfusion.dashloader.blockstates.properties.value.DashPropertyValue;
import net.quantumfusion.dashloader.common.DashID;
import net.quantumfusion.dashloader.common.DashIdentifier;
import net.quantumfusion.dashloader.font.fonts.DashBitmapFont;
import net.quantumfusion.dashloader.font.fonts.DashBlankFont;
import net.quantumfusion.dashloader.font.fonts.DashFont;
import net.quantumfusion.dashloader.font.fonts.DashUnicodeFont;
import net.quantumfusion.dashloader.mixin.NativeImageAccessor;
import net.quantumfusion.dashloader.models.DashModel;
import net.quantumfusion.dashloader.models.DashModelIdentifier;
import net.quantumfusion.dashloader.models.predicates.DashPredicate;
import net.quantumfusion.dashloader.models.predicates.PredicateHelper;
import net.quantumfusion.dashloader.registry.*;
import net.quantumfusion.dashloader.util.ThreadHelper;
import net.quantumfusion.dashloader.util.UndashTask;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DashRegistry {


    private static final int totalTasks = 6;
    private static int tasksDone = 0;
    public Map<Class, Integer> modelsFailed = new ConcurrentHashMap<>();
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
    private final Map<Long, DashModel> models;
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

    public Map<Long, DashProperty> getPropertiesRaw() {
        return properties;
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
            DashModelFactory model = loader.modelMappings.get(bakedModel.getClass());
            if (model != null) {
                models.put(hash, model.toDash(bakedModel, this, var));
            } else {
                Integer integer = modelsFailed.get(bakedModel.getClass());
                if (integer != null) {
                    integer += 1;
                } else {
                    modelsFailed.put(bakedModel.getClass(), 0);

                }
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
            predicates.put(hash, PredicateHelper.getPredicate(selector, stateManager, this));
        }
        return hash;
    }

    public final long createFontPointer(final Font font) {
        final long hash = font.hashCode();
        if (fonts.get(hash) == null) {
            if (font instanceof BitmapFont) {
                fonts.put(hash, new DashBitmapFont((BitmapFont) font, this));
            } else if (font instanceof UnicodeTextureFont) {
                fonts.put(hash, new DashUnicodeFont((UnicodeTextureFont) font, this));
            } else if (font instanceof BlankFont) {
                fonts.put(hash, new DashBlankFont());
            } else {
                DashLoader.LOGGER.warn(font.getClass().getName() + " is not a supported font format, please contact mod developer to add support.");
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
            DashPropertyFactory factory = loader.propertyMappings.get(property.getClass());
            if (factory != null) {
                if (prop) {
                    properties.put(hashP, factory.toDash(property, this, hashP));
                }
                if (propVal) {
                    propertyValues.put(hashV, factory.toDash(value, this, hashP));
                }
            } else {
                DashLoader.LOGGER.warn(property.getClass().getName() + " is not a supported property format, please contact mod developer to add support.");
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
        return predicateOut.get(pointer);
    }

    public final Pair<Property<?>, Comparable<?>> getProperty(final Long propertyPointer, final Long valuePointer) {
        final Property<?> property = propertiesOut.get(propertyPointer);
        final Comparable<?> value = propertyValuesOut.get(valuePointer);
        if (property == null && value == null) {
            DashLoader.LOGGER.error("Property not found in data. PINTR: " + propertyPointer + "/" + valuePointer);
        }
        return Pair.of(property, value);
    }

    public void toUndash() {
        Logger logger = LogManager.getLogger();

        log(logger, "Loading Simple Objects");
        identifiersOut = ThreadHelper.execParallel(identifiers, this);
        identifiers = null;
        imagesOut = ThreadHelper.execParallel(images, this);
        images = null;

        propertiesOut = ThreadHelper.execParallel(properties, this);
        propertyValuesOut = ThreadHelper.execParallel(propertyValues, this);
        properties = null;
        propertyValues = null;

        log(logger, "Loading Advanced Objects");
        blockstatesOut = ThreadHelper.execParallel(blockstates, this);
        blockstates = null;
        predicateOut = ThreadHelper.execParallel(predicates, this);
        predicates = null;
        spritesOut = ThreadHelper.execParallel(sprites, this);
        sprites = null;
        fontsOut = ThreadHelper.execParallel(fonts, this);
        fonts = null;

        modelsOut = new ConcurrentHashMap<>(models.size());
        final short[] currentStage = {0};
        modelsToDeserialize.forEach(modelCategory -> {
            log(logger, "[" + currentStage[0] + "] Loading " + modelCategory.size() + " Models");
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

}
