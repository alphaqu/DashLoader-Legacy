package net.quantumfusion.dash.cache;

import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.atlas.DashExtraAtlasData;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasTextureData;
import net.quantumfusion.dash.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dash.cache.font.DashFontManagerData;
import net.quantumfusion.dash.cache.misc.DashParticleData;
import net.quantumfusion.dash.cache.models.DashModelData;
import net.quantumfusion.dash.mixin.AbstractTextureAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.quantumfusion.dash.Dash.*;

public class DashCache {
    public static final Logger LOGGER = LogManager.getLogger();
    public DashCacheState state;
    //output
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;
    public Map<Identifier, List<Sprite>> particlesOut;
    public Map<Identifier, List<Font>> fontsOut;


    private final List<SpriteAtlasTexture> atlasesToRegister;


    //toserialize
    private SpriteAtlasManager atlasManager;
    private Object2IntMap<BlockState> stateLookup;
    private Map<Identifier, BakedModel> models;
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> particleSprites;
    private SpriteAtlasTexture particleAtlas;
    private List<SpriteAtlasTexture> extraAtlases;
    private Map<Identifier, List<Font>> fonts;

    public HashMap<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();

    public DashCache() {
        LOGGER.info("Creating DashCache Instance");
        extraAtlases = new ArrayList<>();
        atlasesToRegister = new ArrayList<>();
    }

    public void addExtraAtlas(SpriteAtlasTexture atlas) {
        extraAtlases.add(atlas);
    }


    public void addBakedModelAssets(SpriteAtlasManager atlasManager,
                                    Object2IntMap<BlockState> stateLookup,
                                    Map<Identifier, BakedModel> models) {
        this.atlasManager = atlasManager;
        this.models = models;
        this.stateLookup = stateLookup;
    }

    public void addParticleManagerAssets(Map<Identifier, ParticleManager.SimpleSpriteProvider> particles, SpriteAtlasTexture atlas) {
        this.particleSprites = particles;
        particleAtlas = atlas;
    }

    public void addFontAssets(Map<Identifier, List<Font>> fonts) {
        this.fonts = fonts;
    }

    public void serialize() {
        DashRegistry registry = new DashRegistry(this);
        LOGGER.info("Mapping BakedModelAtlas");
        serializeObject(new DashSpriteAtlasManager(atlasManager, atlasData, registry), atlasPath, "BakedModelAtlas");
        LOGGER.info("Mapping Blockstates");
        serializeObject(new DashBlockStateData(stateLookup, registry), blockstatePath, "BlockState");
        LOGGER.info("Mapping Models");
        serializeObject(new DashModelData(models, registry), modelPath, "Model");
        LOGGER.info("Mapping Particles");
        serializeObject(new DashParticleData(particleSprites, particleAtlas, registry), particlePath, "Particle");
        LOGGER.info("Mapping Fonts");
        serializeObject(DashFontManagerData.toDash(fonts, registry), fontPath, "Font");
        LOGGER.info("Mapping Extra Atlases");
        DashExtraAtlasData extraAtlasData = new DashExtraAtlasData();
        this.extraAtlases.forEach(spriteAtlasTexture -> extraAtlasData.addAtlas(spriteAtlasTexture, registry));
        serializeObject(extraAtlasData, extraAtlasPath, "Extra Atlas");
        serializeObject(registry, registryPath, "Registry");
    }


    public void init() {
        if (atlasPath.toFile().exists() && blockstatePath.toFile().exists() && modelPath.toFile().exists() && registryPath.toFile().exists() && particlePath.toFile().exists()) {
            try {
                DashRegistry registry = deserialize(DashRegistry.class, registryPath, "Registry");
                LOGGER.info("      Loading Registry");
                registry.toUndash(this);


                DashSpriteAtlasManager atlas = deserialize(DashSpriteAtlasManager.class, atlasPath, "BakedModel Atlas");
                LOGGER.info("      Loading BakedModel Atlas");
                atlasManagerOut = atlas.toUndash(registry);
                ((SpriteAtlasManagerAccessor) atlasManagerOut).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlasesToRegister.add(spriteAtlasTexture));


                DashBlockStateData blockStateData = deserialize(DashBlockStateData.class, blockstatePath, "BlockState");
                LOGGER.info("      Loading BlockState Data");
                stateLookupOut = blockStateData.toUndash(registry);


                DashParticleData particleData = deserialize(DashParticleData.class, particlePath, "Particle");
                LOGGER.info("      Loading Particle Data");
                Pair<Map<Identifier, List<Sprite>>, SpriteAtlasTexture> outParticle = particleData.toUndash(registry);
                particlesOut = outParticle.getLeft();
                atlasesToRegister.add(outParticle.getValue());


                DashModelData modelData = deserialize(DashModelData.class, modelPath, "Model");
                LOGGER.info("    Loading Model Data");
                modelsOut = modelData.toUndash(registry);
                //load other models

                //TODO find out if these are ever used (2/2)
//                Dash.LOGGER.info("Loading blockstates");
//                blockStateData.toUndash();

                DashExtraAtlasData extraAtlasdata = deserialize(DashExtraAtlasData.class, extraAtlasPath, "Extra Atlas");
                LOGGER.info("      Loading Extra Atlas Data");
                atlasesToRegister.addAll(extraAtlasdata.toUndash(registry));

                DashFontManagerData fontData = deserialize(DashFontManagerData.class, fontPath, "Font");
                LOGGER.info("    Loading Model Data");
                fontsOut = fontData.toUndash(registry);

                LOGGER.info("    Loaded DashCache");
                stateLookupOut = new Object2IntOpenHashMap<>();
                state = DashCacheState.LOADED;
            } catch (Exception e) {
                destroyCache(e);
                state = DashCacheState.CRASHED;
            }
        } else {
            LOGGER.warn("DashCache files missing, Cache creation is appending and slow start predicted.");
        }
    }


    private <C> C deserialize(Class<C> clazz, Path path, String name) {
        try {
            LOGGER.info("  Starting " + name + " Deserialization.");
            //noinspection unchecked
            C out = (C) StreamInput.create(Files.newInputStream(path)).deserialize(serializers.get(clazz));
            if (out == null) {
                throw new DashException(name + " Deserialization failed");
            }
            LOGGER.info("    Finished " + name + " Deserialization.");
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <C> void serializeObject(C clazz, Path path, String name) {
        try {
            LOGGER.info("  Starting " + name + " Serialization.");
            StreamOutput output = StreamOutput.create(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            //noinspection unchecked
            output.serialize(serializers.get(clazz.getClass()), clazz);
            output.close();

            LOGGER.info("    Finished " + name + " Serialization.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void load(TextureManager textureManager) {
        //register textures
        atlasesToRegister.forEach((spriteAtlasTexture) -> {
            //atlas registration
            final DashSpriteAtlasTextureData data = atlasData.get(spriteAtlasTexture);
            final int glId = TextureUtil.generateId();
            final Identifier id = spriteAtlasTexture.getId();
            final int width = data.width;
            final int maxLevel = data.maxLevel;
            final int height = data.height;
            LOGGER.info("Allocated: {}x{}x{} {}-atlas", width, height, maxLevel, id);
            TextureUtil.allocate(glId, maxLevel, width, height);
            ((AbstractTextureAccessor) spriteAtlasTexture).setGlId(glId);
            //ding dong lwjgl here are their styles
            ((SpriteAtlasTextureAccessor) spriteAtlasTexture).getSprites().forEach((identifier1, sprite) -> sprite.upload());
            //helu textures here are the atlases
            textureManager.registerTexture(id, spriteAtlasTexture);
        });
    }


    public void destroyCache(Exception exception) {
        LOGGER.error("DashCache failed, destroying cache and requesting recache. Slow start predicted.");
        exception.printStackTrace();
//        if (!atlasPath.toFile().delete() || !modelPath.toFile().delete() || !registryPath.toFile().delete()|| !blockstatePath.toFile().delete() || !extraAtlasPath.toFile().delete()|| !particlePath.toFile().delete()) {
//            LOGGER.fatal("DashCache removal failed, Closing because of corruption chance, please delete the cache manually or reinstall DashLoader.");
//        }
    }

}
