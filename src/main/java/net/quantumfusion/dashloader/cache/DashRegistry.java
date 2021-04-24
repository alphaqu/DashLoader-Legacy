package net.quantumfusion.dashloader.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
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
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashException;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.atlas.DashImage;
import net.quantumfusion.dashloader.cache.atlas.DashSprite;
import net.quantumfusion.dashloader.cache.blockstates.DashBlockState;
import net.quantumfusion.dashloader.cache.font.fonts.*;
import net.quantumfusion.dashloader.cache.models.DashModel;
import net.quantumfusion.dashloader.cache.models.DashModelIdentifier;
import net.quantumfusion.dashloader.cache.models.ModelStage;
import net.quantumfusion.dashloader.cache.models.factory.DashModelFactory;
import net.quantumfusion.dashloader.cache.models.predicates.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
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
    public Map<Integer, DashPredicate>predicates;

    DashLoader loader;
    public Map<Integer, BlockState> blockstatesOut;
    public Map<Integer, Predicate<BlockState>> predicateOut;
    public Map<Integer, Identifier> identifiersOut;
    public Map<Integer, BakedModel> modelsOut;
    public Map<Integer, Font> fontsOut;
    public Map<Integer, NativeImage> imagesOut;

    public DashRegistry(@Deserialize("blockstates") Map<Integer, DashBlockState> blockstates,
                        @Deserialize("sprites") Map<Integer, DashSprite> sprites,
                        @Deserialize("identifiers") Map<Integer, DashID> identifiers,
                        @Deserialize("models") Map<Integer, DashModel> models,
                        @Deserialize("fonts") Map<Integer, DashFont> fonts,
                        @Deserialize("images") Map<Integer, DashImage> images,
                        @Deserialize("predicates")  Map<Integer, DashPredicate> predicates) {
        this.blockstates = blockstates;
        this.sprites = sprites;
        this.identifiers = identifiers;
        this.models = models;
        this.fonts = fonts;
        this.images = images;
        this.predicates = predicates;
    }

    public DashRegistry(DashLoader loader) {
        blockstates = new HashMap<>();
        sprites = new HashMap<>();
        identifiers = new HashMap<>();
        models = new HashMap<>();
        fonts = new HashMap<>();
        predicates = new HashMap<>();
        images = new HashMap<>();
        this.loader = loader;
    }


    public int createBlockStatePointer(BlockState blockState) {
        final int hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState,this));
        }
        return hash;
    }

    public <K> Integer createModelPointer(BakedModel bakedModel,@Nullable K var) {
        if (bakedModel == null) {
            return null;
        }
        final int hash = bakedModel.hashCode();
        if (models.get(hash) == null) {
            DashModelFactory model = loader.modelMappings.get(bakedModel.getClass());
            if (model != null) {
                models.put(hash, model.toDash(bakedModel, this, var));
            } else {
                if (bakedModel.getClass().getName().contains("wraith")) {
                    DashLoader.LOGGER.error("DH mods dont work with dash because DH is lazy, spam bot him at DH#9367");
                } else {
                    DashLoader.LOGGER.warn(bakedModel.getClass().getName() + " is not a supported model format, please contact mod developer to add support.");
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
            predicates.put(hash, PredicateHelper.getPredicate(selector,stateManager));
        }
        return hash;
    }

    public int createFontPointer(Font font) {
        final int hash = font.hashCode();
        if (fonts.get(hash) == null) {
            if (font instanceof UnicodeFont) {
                fonts.put(hash, new DashUnicodeFont((UnicodeFont) font, this));
            } else if (font instanceof BitmapFont) {
                fonts.put(hash, new DashBitmapFont((BitmapFont) font,this));
            } else if (font instanceof net.minecraft.client.font.BitmapFont) {
                fonts.put(hash, new DashBitmapFont(new BitmapFont((net.minecraft.client.font.BitmapFont) font),this));
            } else if (font instanceof BlankFont) {
                fonts.put(hash, new DashBlankFont());
            } else {
                DashLoader.LOGGER.warn(font.getClass().getName() + " is not a supported font format, please contact mod developer to add support.");
            }
        }
        return hash;
    }


    public BlockState getBlockstate(Integer pointer) {
        if (blockstatesOut == null) {
            throw new DashException("Registry not deserialized");
        }
        BlockState blockstate = blockstatesOut.get(pointer);
        if (blockstate == null) {
            DashLoader.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public Sprite getSprite(Integer pointer) {
        if (spritesOut == null) {
            throw new DashException("Registry not deserialized");
        }
        Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            DashLoader.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public Identifier getIdentifier(Integer pointer) {
        if (identifiersOut == null) {
            throw new DashException("Registry not deserialized");
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
        return predicate;
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
        logger.info("Loading Identifiers");
        identifiers.entrySet().parallelStream().forEach(identifierEntry -> identifiersOut.put(identifierEntry.getKey(), identifierEntry.getValue().toUndash()));
        identifiers = null;

        logger.info("Loading Images");
        images.entrySet().parallelStream().forEach(fontEntry -> imagesOut.put(fontEntry.getKey(), fontEntry.getValue().toUndash()));
        images = null;

        logger.info("Loading Blockstates");
        blockstates.entrySet().parallelStream().forEach(blockstateEntry -> blockstatesOut.put(blockstateEntry.getKey(), blockstateEntry.getValue().toUndash(this)));
        blockstates = null;

        logger.info("Loading Sprites");
        sprites.entrySet().parallelStream().forEach(spriteEntry -> spritesOut.put(spriteEntry.getKey(), spriteEntry.getValue().toUndash(this)));
        sprites = null;

        logger.info("Loading Predicates");
        predicates.entrySet().parallelStream().forEach(predicateEntry -> predicateOut.put(predicateEntry.getKey(), predicateEntry.getValue().toUndash()));
        predicates = null;

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
