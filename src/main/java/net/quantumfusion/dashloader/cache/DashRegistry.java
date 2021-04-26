package net.quantumfusion.dashloader.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
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
    public Map<Integer, DashBlockState> blockstates;


    @Serialize(order = 1)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, DashSprite> sprites;

    public Map<Integer, Sprite> spritesOut;


    @Serialize(order = 2)
    @SerializeSubclasses(path = {1}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {0})
    public Map<Integer, DashID> identifiers;


    @Serialize(order = 3)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "models")
    public Map<Integer, DashModel> models;

    @Serialize(order = 4)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "fonts")
    public Map<Integer, DashFont> fonts;

    @Serialize(order = 5)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Integer, DashImage> images;





    @Serialize(order = 6)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, value = {
            DashAndPredicate.class,
            DashSimplePredicate.class,
            DashOrPredicate.class,
            DashStaticPredicate.class
    })
    public Map<Integer, DashPredicate> predicates;



    @Serialize(order = 7)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, value = {
            DashBooleanProperty.class,
            DashEnumProperty.class,
            DashDirectionProperty.class,
            DashIntProperty.class
    })
    public Map<Integer, DashProperty> properties;


    @Serialize(order = 8)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, value = {
            DashBooleanValue.class,
            DashEnumValue.class,
            DashDirectionValue.class,
            DashIntValue.class
    })
    public Map<Integer, DashPropertyValue> propertyValues;

    public List<Integer> failedPredicates = Collections.synchronizedList(new ArrayList<>());


    public Map<Class, Integer> modelsFailed = new ConcurrentHashMap<>();
    public Map<Integer, BlockState> blockstatesOut;
    public Map<Integer, Predicate<BlockState>> predicateOut;
    public Map<Integer, Identifier> identifiersOut;
    public Map<Integer, BakedModel> modelsOut;
    public Map<Integer, Font> fontsOut;
    public Map<Integer, NativeImage> imagesOut;
    public Map<Integer, Property<?>> propertiesOut;
    public Map<Integer, Comparable<?>> propertyValuesOut;
    DashLoader loader;

    public DashRegistry(@Deserialize("blockstates") Map<Integer, DashBlockState> blockstates,
                        @Deserialize("sprites") Map<Integer, DashSprite> sprites,
                        @Deserialize("identifiers") Map<Integer, DashID> identifiers,
                        @Deserialize("models") Map<Integer, DashModel> models,
                        @Deserialize("fonts") Map<Integer, DashFont> fonts,
                        @Deserialize("images") Map<Integer, DashImage> images,
                        @Deserialize("predicates") Map<Integer, DashPredicate> predicates,
                        @Deserialize("properties") Map<Integer, DashProperty> properties,
                        @Deserialize("propertyValues") Map<Integer, DashPropertyValue> propertyValues) {
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


    public int createBlockStatePointer(BlockState blockState) {
        final int hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState, this));
        }
        return hash;
    }

    public <K> Integer createModelPointer(BakedModel bakedModel, @Nullable K var) {
        if (bakedModel == null) {
            return null;
        }
        final int hash = bakedModel.hashCode();
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

    public int createSpritePointer(Sprite sprite) {
        final int hash = sprite.hashCode();
        if (sprites.get(hash) == null) {
            sprites.put(hash, new DashSprite(sprite, this));
        }
        return hash;
    }

    public int createIdentifierPointer(Identifier identifier) {
        final int hash = identifier.hashCode();
        if (identifiers.get(hash) == null) {
            if (identifier instanceof ModelIdentifier) {
                identifiers.put(hash, new DashModelIdentifier((ModelIdentifier) identifier));
            } else {
                identifiers.put(hash, new DashIdentifier(identifier));
            }
        }
        return hash;
    }

    public int createImagePointer(NativeImage image) {
        final int hash = image.hashCode();
        if (images.get(hash) == null) {
            images.put(hash, new DashImage(image));
        }
        return hash;
    }

    public int createPredicatePointer(MultipartModelSelector selector, StateManager<Block, BlockState> stateManager) {
        final int hash = selector.hashCode();
        if (predicates.get(hash) == null) {
            predicates.put(hash, PredicateHelper.getPredicate(selector, stateManager,this));
        }
        return hash;
    }

    public int createFontPointer(Font font) {
        final int hash = font.hashCode();
        if (fonts.get(hash) == null) {
            if (font instanceof UnicodeFont) {
                fonts.put(hash, new DashUnicodeFont((UnicodeFont) font, this));
            } else if (font instanceof BitmapFont) {
                fonts.put(hash, new DashBitmapFont((BitmapFont) font, this));
            } else if (font instanceof net.minecraft.client.font.BitmapFont) {
                fonts.put(hash, new DashBitmapFont(new BitmapFont((net.minecraft.client.font.BitmapFont) font), this));
            } else if (font instanceof BlankFont) {
                fonts.put(hash, new DashBlankFont());
            } else {
                DashLoader.LOGGER.warn(font.getClass().getName() + " is not a supported font format, please contact mod developer to add support.");
            }
        }
        return hash;
    }

    public Pair<Integer,Integer> createPropertyPointer(Property<?> property, Comparable<?> value) {
        final int hashV = value.hashCode();
        final int hashP = property.hashCode();
        propertyValues.putIfAbsent(hashV,PredicateHelper.getPropertyValue(value,hashP));
        properties.putIfAbsent(hashP,PredicateHelper.getProperty(property));
        return Pair.of(hashP,hashV);
    }


    public BlockState getBlockstate(Integer pointer) {
        if (blockstatesOut == null) {
            throw new DashException("BlockStates not deserialized");
        }
        BlockState blockstate = blockstatesOut.get(pointer);
        if (blockstate == null) {
            DashLoader.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public Sprite getSprite(Integer pointer) {
        if (spritesOut == null) {
            throw new DashException("Sprites not deserialized");
        }
        Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            DashLoader.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public Identifier getIdentifier(Integer pointer) {
        if (identifiersOut == null) {
            throw new DashException("Identifiers not deserialized");
        }
        Identifier identifier = identifiersOut.get(pointer);
        if (identifier == null) {
            DashLoader.LOGGER.error("Identifier not found in data. PINTR: " + pointer);
        }
        return identifier;
    }

    public BakedModel getModel(Integer pointer) {
        if (modelsOut == null) {
            throw new DashException("Models not deserialized");
        }
        BakedModel bakedModel = modelsOut.get(pointer);
        if (bakedModel == null) {
            DashLoader.LOGGER.error("Model not found in data. PINTR: " + pointer);
        }
        return bakedModel;
    }

    public Font getFont(Integer pointer) {
        if (fontsOut == null) {
            throw new DashException("Fonts not deserialized");
        }
        Font font = fontsOut.get(pointer);
        if (font == null) {
            DashLoader.LOGGER.error("Font not found in data. PINTR: " + pointer);
        }
        return font;
    }

    public NativeImage getImage(Integer pointer) {
        if (imagesOut == null) {
            throw new DashException("NativeImages not deserialized");
        }
        NativeImage image = imagesOut.get(pointer);
        if (image == null) {
            DashLoader.LOGGER.error("NativeImage not found in data. PINTR: " + pointer);
        }
        return image;
    }

    public Predicate<BlockState> getPredicate(Integer pointer) {
        if (predicateOut == null) {
            throw new DashException("Predicates not deserialized");
        }
        Predicate<BlockState> predicate = predicateOut.get(pointer);
        if (predicate == null) {
            DashLoader.LOGGER.error("Predicate not found in data. PINTR: " + pointer);
        }
        return predicateOut.get(pointer);
    }

    public Pair<Property<?>, Comparable<?>> getProperty(int propertyPointer, int valuePointer) {
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
