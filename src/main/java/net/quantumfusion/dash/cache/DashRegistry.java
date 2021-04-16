package net.quantumfusion.dash.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.atlas.DashImage;
import net.quantumfusion.dash.cache.atlas.DashSprite;
import net.quantumfusion.dash.cache.blockstates.DashBlockState;
import net.quantumfusion.dash.cache.font.fonts.*;
import net.quantumfusion.dash.cache.models.DashModel;
import net.quantumfusion.dash.cache.models.DashModelIdentifier;
import net.quantumfusion.dash.cache.models.ModelStage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public Map<Integer, DashImage> fontImages;

    DashCache loader;
    public Map<Integer, BlockState> blockstatesOut;
    public Map<Integer, Predicate<BlockState>> predicateOut;
    public Map<Integer, Identifier> identifiersOut;
    public Map<Integer, BakedModel> modelsOut;
    public Map<Integer, Font> fontsOut;
    public Map<Integer, NativeImage> fontImagesOut;

    public DashRegistry(@Deserialize("blockstates") Map<Integer, DashBlockState> blockstates,
                        @Deserialize("sprites") Map<Integer, DashSprite> sprites,
                        @Deserialize("identifiers") Map<Integer, DashID> identifiers,
                        @Deserialize("models") Map<Integer, DashModel> models,
                        @Deserialize("fonts") Map<Integer, DashFont> fonts,
                        @Deserialize("fontImages") Map<Integer, DashImage> fontImages) {
        this.blockstates = blockstates;
        this.sprites = sprites;
        this.identifiers = identifiers;
        this.models = models;
        this.fonts = fonts;
        this.fontImages = fontImages;
    }

    public DashRegistry(DashCache loader) {
        blockstates = new HashMap<>();
        sprites = new HashMap<>();
        identifiers = new HashMap<>();
        models = new HashMap<>();
        fonts = new HashMap<>();
        fontImages = new HashMap<>();
        this.loader = loader;
    }


    public int createBlockStatePointer(BlockState blockState) {
        final int hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState,this));
        }
        return hash;
    }

    public Integer createModelPointer(BakedModel bakedModel) {
        if (bakedModel == null) {
            return null;
        }
        final int hash = bakedModel.hashCode();
        if (models.get(hash) == null) {
            DashModel model = Dash.modelMappings.get(bakedModel.getClass());
            if (model != null) {
                models.put(hash, model.toDash(bakedModel, this));
            } else {
                if (bakedModel.getClass().getName().contains("wraith")) {
                    DashCache.LOGGER.error("DH mods dont work with dash because DH is lazy, spam bot him at DH#9367");
                } else {
                    DashCache.LOGGER.warn(bakedModel.getClass().getName() + " is not a supported model format, please contact mod developer to add support.");
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

    public int createFontImagePointer(NativeImage image) {
        final int hash = image.hashCode();
        if (fontImages.get(hash) == null) {
            fontImages.put(hash, new DashImage(image));
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
                DashCache.LOGGER.warn(font.getClass().getName() + " is not a supported font format, please contact mod developer to add support.");
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
            Dash.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public Sprite getSprite(Integer pointer) {
        if (spritesOut == null) {
            throw new DashException("Registry not deserialized");
        }
        Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            Dash.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public Identifier getIdentifier(Integer pointer) {
        if (identifiersOut == null) {
            throw new DashException("Registry not deserialized");
        }
        Identifier identifier = identifiersOut.get(pointer);
        if (identifier == null) {
            Dash.LOGGER.error("Identifier not found in data. PINTR: " + pointer);
        }
        return identifier;
    }

    public BakedModel getModel(Integer pointer) {
        if (modelsOut == null) {
            throw new DashException("Models not deserialized");
        }
        BakedModel bakedModel = modelsOut.get(pointer);
        if (bakedModel == null) {
            Dash.LOGGER.error("Model not found in data. PINTR: " + pointer);
        }
        return bakedModel;
    }


    public Font getFont(Integer pointer) {
        if (fontsOut == null) {
            throw new DashException("Fonts not deserialized");
        }
        Font font = fontsOut.get(pointer);
        if (font == null) {
            Dash.LOGGER.error("Font not found in data. PINTR: " + pointer);
        }
        return font;
    }

    public NativeImage getFontImage(Integer pointer) {
        if (fontsOut == null) {
            throw new DashException("FontImages not deserialized");
        }
        NativeImage fontImage = fontImagesOut.get(pointer);
        if (fontImage == null) {
            Dash.LOGGER.error("FontImage not found in data. PINTR: " + pointer);
        }
        return fontImage;
    }

    public void toUndash(DashCache loader) {
        Logger logger = LogManager.getLogger();
        spritesOut = new ConcurrentHashMap<>();
        blockstatesOut = new ConcurrentHashMap<>();
        predicateOut = new ConcurrentHashMap<>();
        identifiersOut = new ConcurrentHashMap<>();
        fontImagesOut = new ConcurrentHashMap<>();
        modelsOut = new ConcurrentHashMap<>();
        fontsOut = new ConcurrentHashMap<>();
        logger.info("Loading Identifiers");
        identifiers.entrySet().parallelStream().forEach(identifierEntry -> identifiersOut.put(identifierEntry.getKey(), identifierEntry.getValue().toUndash()));
        logger.info("Loading Blockstates");
        blockstates.entrySet().parallelStream().forEach(blockstateEntry -> {
            final Pair<BlockState, Predicate<BlockState>> out = blockstateEntry.getValue().toUndash(this);
            final Integer key = blockstateEntry.getKey();
            blockstatesOut.put(key, out.getKey());
            predicateOut.put(key, out.getValue());
        });
        logger.info("Loading Sprites");
        sprites.entrySet().parallelStream().forEach(spriteEntry -> spritesOut.put(spriteEntry.getKey(), spriteEntry.getValue().toUndash(this)));


        logger.info("Loading FontImage");
        fontImages.entrySet().parallelStream().forEach(fontEntry -> fontImagesOut.put(fontEntry.getKey(), fontEntry.getValue().toUndash()));
        logger.info("Loading Fonts");
        fonts.entrySet().parallelStream().forEach(fontEntry -> fontsOut.put(fontEntry.getKey(), fontEntry.getValue().toUndash(this)));

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
    }

}
