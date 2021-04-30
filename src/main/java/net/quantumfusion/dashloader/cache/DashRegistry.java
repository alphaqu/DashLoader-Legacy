package net.quantumfusion.dashloader.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.IntLists;
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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashException;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.atlas.DashImage;
import net.quantumfusion.dashloader.cache.atlas.DashSprite;
import net.quantumfusion.dashloader.cache.blockstates.DashBlockState;
import net.quantumfusion.dashloader.cache.blockstates.properties.*;
import net.quantumfusion.dashloader.cache.blockstates.properties.value.*;
import net.quantumfusion.dashloader.cache.font.fonts.*;
import net.quantumfusion.dashloader.cache.models.DashModel;
import net.quantumfusion.dashloader.cache.models.DashModelIdentifier;
import net.quantumfusion.dashloader.cache.models.ModelStage;
import net.quantumfusion.dashloader.cache.models.factory.DashModelFactory;
import net.quantumfusion.dashloader.cache.models.predicates.*;
import net.quantumfusion.dashloader.mixin.NativeImageAccessor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DashRegistry {


    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Long, DashBlockState> blockstates;


    @Serialize(order = 1)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Long, DashSprite> sprites;

    public Map<Long, Sprite> spritesOut;


    @Serialize(order = 2)
    @SerializeSubclasses(path = {1}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {0})
    public Map<Long, DashID> identifiers;


    @Serialize(order = 3)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "models")
    public Map<Long, DashModel> models;

    @Serialize(order = 4)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "fonts")
    public Map<Long, DashFont> fonts;

    @Serialize(order = 5)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Long, DashImage> images;





    @Serialize(order = 6)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, value = {
            DashAndPredicate.class,
            DashSimplePredicate.class,
            DashOrPredicate.class,
            DashStaticPredicate.class
    })
    public Map<Long, DashPredicate> predicates;



    @Serialize(order = 7)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, value = {
            DashBooleanProperty.class,
            DashEnumProperty.class,
            DashDirectionProperty.class,
            DashIntProperty.class
    })
    public Map<Long, DashProperty> properties;


    @Serialize(order = 8)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, value = {
            DashBooleanValue.class,
            DashEnumValue.class,
            DashDirectionValue.class,
            DashIntValue.class
    })
    public Map<Long, DashPropertyValue> propertyValues;

    public List<Integer> failedPredicates = Collections.synchronizedList(new ArrayList<>());


    public Map<Class, Integer> modelsFailed = new ConcurrentHashMap<>();
    public Map<Long, BlockState> blockstatesOut;
    public Map<Long, Predicate<BlockState>> predicateOut;
    public Map<Long, Identifier> identifiersOut;
    public Map<Long, BakedModel> modelsOut;
    public Map<Long, Font> fontsOut;
    public Map<Long, NativeImage> imagesOut;
    public Map<Long, Property<?>> propertiesOut;
    public Map<Long, Comparable<?>> propertyValuesOut;
    DashLoader loader;

    public DashRegistry(@Deserialize("blockstates") Map<Long, DashBlockState> blockstates,
                        @Deserialize("sprites") Map<Long, DashSprite> sprites,
                        @Deserialize("identifiers") Map<Long, DashID> identifiers,
                        @Deserialize("models") Map<Long, DashModel> models,
                        @Deserialize("fonts") Map<Long, DashFont> fonts,
                        @Deserialize("images") Map<Long, DashImage> images,
                        @Deserialize("predicates") Map<Long, DashPredicate> predicates,
                        @Deserialize("properties") Map<Long, DashProperty> properties,
                        @Deserialize("propertyValues") Map<Long, DashPropertyValue> propertyValues) {
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
        this.loader = loader;
    }


    public long createBlockStatePointer(BlockState blockState) {
        final long hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState, this));
        }
        return hash;
    }

    public <K> Long createModelPointer(BakedModel bakedModel, @Nullable K var) {
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

    public long createSpritePointer(Sprite sprite) {
        final long hash = sprite.hashCode();
        if (sprites.get(hash) == null) {
            sprites.put(hash, new DashSprite(sprite, this));
        }
        return hash;
    }

    public long createIdentifierPointer(Identifier identifier) {
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

    public long createImagePointer(NativeImage image) {
        final long hash = ((NativeImageAccessor)(Object)image).getPointer();
        if (images.get(hash) == null) {
            images.put(hash, new DashImage(image));
        }
        return hash;
    }

    public long createPredicatePointer(MultipartModelSelector selector, StateManager<Block, BlockState> stateManager) {
        final long hash = selector.hashCode();
        if (predicates.get(hash) == null) {
            predicates.put(hash, PredicateHelper.getPredicate(selector, stateManager,this));
        }
        return hash;
    }

    public long createFontPointer(Font font) {
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

    public Pair<Long,Long> createPropertyPointer(Property<?> property, Comparable<?> value) {
        final long hashV = value.hashCode();
        final long hashP = property.hashCode();
        propertyValues.putIfAbsent(hashV,PredicateHelper.getPropertyValue(value,hashP));
        properties.putIfAbsent(hashP,PredicateHelper.getProperty(property));
        return Pair.of(hashP,hashV);
    }


    public BlockState getBlockstate(Long pointer) {
        if (blockstatesOut == null) {
            throw new DashException("BlockStates not deserialized");
        }
        BlockState blockstate = blockstatesOut.get(pointer);
        if (blockstate == null) {
            DashLoader.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public Sprite getSprite(Long pointer) {
        if (spritesOut == null) {
            throw new DashException("Sprites not deserialized");
        }
        Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            DashLoader.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public Identifier getIdentifier(Long pointer) {
        if (identifiersOut == null) {
            throw new DashException("Identifiers not deserialized");
        }
        Identifier identifier = identifiersOut.get(pointer);
        if (identifier == null) {
            DashLoader.LOGGER.error("Identifier not found in data. PINTR: " + pointer);
        }
        return identifier;
    }

    public BakedModel getModel(Long pointer) {
        if (modelsOut == null) {
            throw new DashException("Models not deserialized");
        }
        BakedModel bakedModel = modelsOut.get(pointer);
        if (bakedModel == null) {
            DashLoader.LOGGER.error("Model not found in data. PINTR: " + pointer);
        }
        return bakedModel;
    }

    public Font getFont(Long pointer) {
        if (fontsOut == null) {
            throw new DashException("Fonts not deserialized");
        }
        Font font = fontsOut.get(pointer);
        if (font == null) {
            DashLoader.LOGGER.error("Font not found in data. PINTR: " + pointer);
        }
        return font;
    }

    public NativeImage getImage(Long pointer) {
        if (imagesOut == null) {
            throw new DashException("NativeImages not deserialized");
        }
        NativeImage image = imagesOut.get(pointer);
        if (image == null) {
            DashLoader.LOGGER.error("NativeImage not found in data. PINTR: " + pointer);
        }
        return image;
    }

    public Predicate<BlockState> getPredicate(Long pointer) {
        if (predicateOut == null) {
            throw new DashException("Predicates not deserialized");
        }
        Predicate<BlockState> predicate = predicateOut.get(pointer);
        if (predicate == null) {
            DashLoader.LOGGER.error("Predicate not found in data. PINTR: " + pointer);
        }
        return predicateOut.get(pointer);
    }

    public Pair<Property<?>, Comparable<?>> getProperty(Long propertyPointer, Long valuePointer) {
        if (propertiesOut == null) {
            throw new DashException("Properties not deserialized");
        }
        Property<?> property = propertiesOut.get(propertyPointer);
        if (property == null) { DashLoader.LOGGER.error("Property not found in data. PINTR: " + propertyPointer); }
        Comparable<?> value = propertyValuesOut.get(valuePointer);
        if (value == null) { DashLoader.LOGGER.error("PropertyValue not found in data. PINTR: " + valuePointer); }
        return Pair.of(property,value);
    }

    public void toUndash() {
        Logger logger = LogManager.getLogger();
        spritesOut = new ConcurrentHashMap<>();
        blockstatesOut = new ConcurrentHashMap<>();
        predicateOut = new ConcurrentHashMap<>();
        identifiersOut = new ConcurrentHashMap<>();
        imagesOut = new ConcurrentHashMap<>();
        modelsOut = new ConcurrentHashMap<>();
        fontsOut = new ConcurrentHashMap<>();
        propertiesOut = new ConcurrentHashMap<>();
        propertyValuesOut = new ConcurrentHashMap<>();
        logger.info("Loading Identifiers");
        identifiers.entrySet().parallelStream().forEach(identifierEntry -> identifiersOut.put(identifierEntry.getKey(), identifierEntry.getValue().toUndash()));
        identifiers = null;

        logger.info("Loading Images");
        images.entrySet().parallelStream().forEach(fontEntry -> imagesOut.put(fontEntry.getKey(), fontEntry.getValue().toUndash()));
        images = null;

        logger.info("Loading Properties");
        properties.entrySet().parallelStream().forEach(entry -> propertiesOut.put(entry.getKey(),entry.getValue().toUndash()));
        propertyValues.entrySet().parallelStream().forEach(entry -> propertyValuesOut.put(entry.getKey(),entry.getValue().toUndash(this)));
        properties = null;
        propertyValues = null;

        logger.info("Loading Blockstates");
        blockstates.entrySet().parallelStream().forEach(blockstateEntry -> blockstatesOut.put(blockstateEntry.getKey(), blockstateEntry.getValue().toUndash(this)));
        blockstates = null;

        logger.info("Loading Predicates");
        predicates.entrySet().parallelStream().forEach(predicateEntry -> {
            predicateOut.put(predicateEntry.getKey(), predicateEntry.getValue().toUndash(this));
        });
        predicates = null;

        logger.info("Loading Sprites");
        sprites.entrySet().parallelStream().forEach(spriteEntry -> spritesOut.put(spriteEntry.getKey(), spriteEntry.getValue().toUndash(this)));
        sprites = null;


        logger.info("Loading Fonts");
        fonts.entrySet().parallelStream().forEach(fontEntry -> fontsOut.put(fontEntry.getKey(), fontEntry.getValue().toUndash(this)));
        fonts = null;

        logger.info("Loading Simple Models");
        models.entrySet().parallelStream().forEach(modelEntry -> {
            final DashModel value = modelEntry.getValue();
            if (value.getStage() == ModelStage.SIMPLE) {
                modelsOut.put(modelEntry.getKey(), value.toUndash(this));
            }
        });

        logger.info("Loading Intermediate Models");
        models.entrySet().parallelStream().forEach(modelEntry -> {
            final DashModel value = modelEntry.getValue();
            if (value.getStage() == ModelStage.INTERMEDIATE) {
                modelsOut.put(modelEntry.getKey(), value.toUndash(this));
            }
        });

        logger.info("Loading Advanced Models");
        models.entrySet().parallelStream().forEach(modelEntry -> {
            final DashModel value = modelEntry.getValue();
            if (value.getStage() == ModelStage.ADVANCED) {
                modelsOut.put(modelEntry.getKey(), value.toUndash(this));
            }
        });

        logger.info("Applying Model Overrides");
        models.forEach((key, value) -> value.apply(this));
        models = null;
    }

}
