package net.quantumfusion.dash.cache;

import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasTextureData;
import net.quantumfusion.dash.cache.models.*;
import net.quantumfusion.dash.mixin.AbstractTextureAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static net.quantumfusion.dash.Dash.*;

public class DashModelLoader {
    //output
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;
    public boolean loaded = false;


    public DashRegistry registry;

    public HashMap<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();




//    Path blockstatePath = FileSystems.getDefault().getPath("C:\\Program Files (x86)\\inkscape\\MinecraftMods\\Dash\\run\\config\\dash\\blockstate.activej");

    public DashModelLoader() {
    }

    public void serialize(SpriteAtlasManager atlasManager,
                          Object2IntMap<BlockState> stateLookup,
                          Map<Identifier, BakedModel> models) {
        Dash.LOGGER.info("Creating DashRegistry");
        registry = new DashRegistry();
        Dash.LOGGER.info("Mapping Atlas");
        DashSpriteAtlasManager atlas = new DashSpriteAtlasManager(atlasManager, atlasData, this);
//        Dash.LOGGER.info("Mapping Blockstates");
//        DashBlockStateData blockstate = new DashBlockStateData(stateLookup);
        Dash.LOGGER.info("Mapping Models");
        DashModelData model = new DashModelData(models, this);

        Dash.LOGGER.info("Serializing Registry");
        try {

            StreamOutput output = StreamOutput.create(Files.newOutputStream(registryPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            output.serialize(Dash.registrySerializer, registry);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Dash.LOGGER.info("Serializing atlas");
        try {
            StreamOutput output = StreamOutput.create(Files.newOutputStream(atlasPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            output.serialize(Dash.atlasSerializer, atlas);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        Dash.LOGGER.info("Serializing blockstates");
//        try {
//
//            StreamOutput output = StreamOutput.create(Files.newOutputStream(blockstatePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
//            output.serialize(Dash.blockStateSerializer, blockstate);
//            output.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Dash.LOGGER.info("Serializing models");
        try {
            StreamOutput output = StreamOutput.create(Files.newOutputStream(modelPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            output.serialize(Dash.modelSerializer, model);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Dash.LOGGER.info("Caching complete.");
    }

    public DashModel convertSimpleModel(BakedModel model) {
        if (model instanceof BasicBakedModel) {
            return new DashBasicBakedModel((BasicBakedModel) model, this);
        } else if (model instanceof BuiltinBakedModel) {
            return new DashBuiltinBakedModel((BuiltinBakedModel) model, this);
        } else if (model instanceof MultipartBakedModel) {
            return new DashMultipartBakedModel((MultipartBakedModel) model, this);
        } else if (model instanceof WeightedBakedModel) {
            return new DashWeightedBakedModel((WeightedBakedModel) model, this);
        } else {
            if (model != null) {
                String canonicalName = model.getClass().getCanonicalName();
                if (canonicalName.contains("wraith")) {
                    LOGGER.error("DH mods are not supported by Dash, ping him really hard on discord here so he can start working on it: DH#9367");
                } else {
                    LOGGER.error(canonicalName + " is not supported by Dash, ask the developer to add support.");
                }
            }
        }
        return null;
    }


    public void init(ExecutorService threadPool) {
        threadPool.execute(() -> {
            if (atlasPath.toFile().exists() && /*blockstatePath.toFile().exists() && */ modelPath.toFile().exists() && registryPath.toFile().exists()) {

                Dash.LOGGER.info("Deserializing registry");
                try {
                    registry = StreamInput.create(Files.newInputStream(registryPath)).deserialize(Dash.registrySerializer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (registry == null) {
                    throw new DashException("Registry deserialization failed");
                }

                Dash.LOGGER.info("Deserializing atlas");
                DashSpriteAtlasManager atlas = null;
                try {
                    atlas = StreamInput.create(Files.newInputStream(atlasPath)).deserialize(Dash.atlasSerializer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (atlas == null) {
                    throw new DashException("Atlas deserialization failed");
                }


                //TODO find out if these are ever used (1/2)
//                Dash.LOGGER.info("Deserializing blockstates");
//                try {
//                    blockStateData = StreamInput.create(Files.newInputStream(blockstatePath)).deserialize(Dash.blockStateSerializer);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                Dash.LOGGER.info("Deserializing models");
                modelsOut = new HashMap<>();
                DashModelData modelData = null;
                try {
                    modelData = StreamInput.create(Files.newInputStream(modelPath)).deserialize(Dash.modelSerializer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (modelData == null) {
                    throw new DashException("Model deserialization failed");
                }

                Dash.LOGGER.info("Loading registry");
                registry.toUndash();
                Dash.LOGGER.info("Loading atlas");
                atlasManagerOut = atlas.toUndash(this);
                //TODO find out if these are ever used (2/2)
//                Dash.LOGGER.info("Loading blockstates");
//                blockStateData.toUndash();
                //load basicmodels
                Dash.LOGGER.info("Loading simple models");
                modelData.simpleModels.forEach((dashIdentifier, dashBakedModel) -> modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(this)));
                //load other models
                Dash.LOGGER.info("Loading complex models");
                modelData.advancedModels.forEach((dashIdentifier, dashBakedModel) -> {
                    modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(this));
                });
                Dash.LOGGER.info("Loaded DashCache");
                stateLookupOut = new Object2IntOpenHashMap<>();
                loaded = true;
            } else {
                LogManager.getLogger().warn("DashCache files missing, Cache creation is appending and slow start predicted.");
            }
        });
    }

    public void load(TextureManager textureManager) {
        //register textures
        ((SpriteAtlasManagerAccessor) atlasManagerOut).getAtlases().forEach((identifier, spriteAtlasTexture) -> {
            //atlas registration
            DashSpriteAtlasTextureData data = atlasData.get(spriteAtlasTexture);
            int glId = TextureUtil.generateId();
            LogManager.getLogger().info("Allocated: {}x{}x{} {}-atlas", data.width, data.height, data.maxLevel, identifier);
            TextureUtil.allocate(glId, data.maxLevel, data.width, data.height);
            ((AbstractTextureAccessor) spriteAtlasTexture).setGlId(glId);
            //ding dong lwjgl here are their styles
            ((SpriteAtlasTextureAccessor)spriteAtlasTexture).getSprites().forEach((identifier1, sprite) -> sprite.upload());
            //helu textures here are the atlases
            textureManager.registerTexture(identifier,spriteAtlasTexture);
        });
    }


}
