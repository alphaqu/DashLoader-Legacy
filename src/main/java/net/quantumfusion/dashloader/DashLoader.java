package net.quantumfusion.dashloader;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.StringFormat;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.DashCachePaths;
import net.quantumfusion.dashloader.cache.DashCacheState;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.MappingData;
import net.quantumfusion.dashloader.cache.atlas.DashExtraAtlasData;
import net.quantumfusion.dashloader.cache.atlas.DashSpriteAtlasData;
import net.quantumfusion.dashloader.cache.atlas.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dashloader.cache.blockstates.properties.DashBooleanProperty;
import net.quantumfusion.dashloader.cache.blockstates.properties.DashDirectionProperty;
import net.quantumfusion.dashloader.cache.blockstates.properties.DashEnumProperty;
import net.quantumfusion.dashloader.cache.blockstates.properties.DashIntProperty;
import net.quantumfusion.dashloader.cache.blockstates.properties.value.DashBooleanValue;
import net.quantumfusion.dashloader.cache.blockstates.properties.value.DashDirectionValue;
import net.quantumfusion.dashloader.cache.blockstates.properties.value.DashEnumValue;
import net.quantumfusion.dashloader.cache.blockstates.properties.value.DashIntValue;
import net.quantumfusion.dashloader.cache.font.DashFontManagerData;
import net.quantumfusion.dashloader.cache.font.fonts.DashBitmapFont;
import net.quantumfusion.dashloader.cache.font.fonts.DashBlankFont;
import net.quantumfusion.dashloader.cache.font.fonts.DashUnicodeFont;
import net.quantumfusion.dashloader.cache.misc.DashLoaderInfo;
import net.quantumfusion.dashloader.cache.misc.DashParticleData;
import net.quantumfusion.dashloader.cache.models.*;
import net.quantumfusion.dashloader.cache.models.factory.*;
import net.quantumfusion.dashloader.cache.models.predicates.DashAndPredicate;
import net.quantumfusion.dashloader.cache.models.predicates.DashOrPredicate;
import net.quantumfusion.dashloader.cache.models.predicates.DashSimplePredicate;
import net.quantumfusion.dashloader.cache.models.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.mixin.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.util.TextureHelper;
import net.quantumfusion.dashloader.util.TimeHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int totalTasks = 11;
    public static final double formatVersion = 0.4;
    public static final String version = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    private static final Path config = FabricLoader.getInstance().getConfigDir().normalize();
    private static final boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static String task = "Starting DashLoader";
    private static DashLoader instance;
    public final HashMap<Class<? extends BakedModel>, DashModelFactory> modelMappings = new HashMap<>();
    public final HashMap<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    public final HashMap<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();
    private final List<SpriteAtlasTexture> atlasesToRegister;
    private final Map<DashCachePaths, Path> paths = new HashMap<>();
    private final List<SpriteAtlasTexture> extraAtlases;
    public int tasksComplete = 0;
    public DashCacheState state;
    public Map<Identifier, List<Font>> fonts = new HashMap<>();
    //output
    private Object2IntMap<BlockState> stateLookupOut;
    private MappingData mappings = new MappingData();


    public SpriteAtlasManager getAtlasManagerOut() {
        return mappings.atlasManagerOut;
    }

    public Object2IntMap<BlockState> getStateLookupOut() {
        return stateLookupOut;
    }

    public Map<Identifier, BakedModel> getModelsOut() {
        return mappings.modelsOut;
    }

    public Map<Identifier, List<Sprite>> getParticlesOut() {
        return mappings.particlesOut;
    }

    public Map<Identifier, List<Font>> getFontsOut() {
        return mappings.fontsOut;
    }

    public List<String> getSplashTextOut() {
        return mappings.splashTextOut;
    }

    private Object2ObjectMap<Class<?>, BinarySerializer> serializers = new Object2ObjectOpenHashMap<>();
    private SpriteAtlasManager atlasManager;
    private Object2IntMap<BlockState> stateLookup;
    private Map<Identifier, BakedModel> models;
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> particleSprites;
    private SpriteAtlasTexture particleAtlas;
    private List<String> splashText;

    public DashLoader() {
        instance = this;
        LOGGER.info("Creating DashLoader Instance");
        extraAtlases = new ArrayList<>();
        atlasesToRegister = new ArrayList<>();
    }

    public static DashLoader getInstance() {
        return instance;
    }

    public void reload() {
        LOGGER.info("Starting dash thread.");
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.warn("Dashloader launched in dev.");
        }
        state = DashCacheState.LOADING;
        Instant start = Instant.now();
        Thread dash = new Thread(() -> {
            initPaths();
            initModelMappings();
            initSerializers();
            createDirectory();
            LOGGER.info("Checking for Mod Change.");
            DashLoaderInfo newData = DashLoaderInfo.create();
            boolean reload = true;
            try {
                if (paths.get(DashCachePaths.DASH_INFO).toFile().exists()) {
                    DashLoaderInfo old = deserialize(DashLoaderInfo.class, paths.get(DashCachePaths.DASH_INFO), "Mod Info");
                    reload = !newData.equals(old);
                }
            } catch (Exception ignored) {
            }
            if (reload) {
                destroyCache();
                LOGGER.warn("DashLoader detected mod change, Recache requested.");
                state = DashCacheState.EMPTY;
            } else {
                if (paths.values().stream().allMatch(path -> path.toFile().exists())) {
                    loadDashCache();
                } else {
                    destroyCache();
                    LOGGER.warn("DashLoader files missing, Cache creation is appending and slow start predicted.");
                    state = DashCacheState.EMPTY;
                }
            }
            newData = DashLoaderInfo.create();
            createMetadata(newData);
            LOGGER.info("Loaded cache in " + TimeHelper.getDecimalMs(start, Instant.now()) + "s");
        });
        dash.setName("dash-manager");
        dash.start();
    }

    private void createMetadata(DashLoaderInfo data) {
        tasksComplete--;
        serializeObject(data, paths.get(DashCachePaths.DASH_INFO), "Mod Info");
    }

    private void createDirectory() {
        prepareAccess(new File(String.valueOf(config.resolve("quantumfusion/dashloader")))).mkdirs();
    }

    public void addExtraAtlasAssets(SpriteAtlasTexture atlas) {
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

    public void addSplashTextAssets(List<String> splashText) {
        this.splashText = splashText;
    }

    public void saveDashCache() {
        Instant start = Instant.now();
        initPaths();
        initModelMappings();
        initSerializers();
        createDirectory();
        tasksComplete++;
        DashRegistry registry = new DashRegistry(this);
        MappingData mappings = new MappingData();
        logAndTask("Mapping Atlas");
        mappings.setSpriteAtlasData(new DashSpriteAtlasData(atlasManager, atlasData, registry));

        logAndTask("Mapping Blockstates");
        mappings.setBlockStateData(new DashBlockStateData(stateLookup, registry));

        logAndTask("Mapping Models");
        mappings.setModelData(new DashModelData(models, multipartData, registry));

        logAndTask("Mapping Particles");
        mappings.setParticleData(new DashParticleData(particleSprites, particleAtlas, registry));

        logAndTask("Mapping Fonts");
        mappings.setFontManagerData(new DashFontManagerData(fonts, registry));

        logAndTask("Mapping Splash Text");
        mappings.setSplashTextData(new DashSplashTextData(splashText));

        logAndTask("Mapping Extra Atlases");
        tasksComplete++;
        DashExtraAtlasData extraAtlasData = new DashExtraAtlasData();
        this.extraAtlases.forEach(spriteAtlasTexture -> extraAtlasData.addAtlas(spriteAtlasTexture, registry));
        mappings.setExtraAtlasData(extraAtlasData);

        serializeObject(mappings, paths.get(DashCachePaths.MAPPINGS), "Mappings");
        serializeObject(registry, paths.get(DashCachePaths.REGISTRY), "Registry");
        task = "Caching is now complete.";
        LOGGER.info("Created cache in " + TimeHelper.getDecimalMs(start, Instant.now()) + "s");
    }

    public void loadDashCache() {
        LOGGER.info("Starting DashLoader Deserialization");
        try {
            DashRegistry registry = deserialize(DashRegistry.class, paths.get(DashCachePaths.REGISTRY), "Registry");
            LOGGER.info("      Loading Registry");
            registry.toUndash();

            mappings = deserialize(MappingData.class, paths.get(DashCachePaths.MAPPINGS), "Mappings");
            LOGGER.info("      Loading Mappings");
            atlasesToRegister.addAll(mappings.toUndash(registry));

            LOGGER.info("    Loaded DashLoader");
            stateLookupOut = new Object2IntOpenHashMap<>();
            state = DashCacheState.LOADED;
        } catch (Exception e) {
            destroyCache(e);
            state = DashCacheState.CRASHED;
        }
    }

    public void applyDashCache(TextureManager textureManager) {
        //register textures
        atlasesToRegister.forEach((spriteAtlasTexture) -> {
            //atlas registration
            final DashSpriteAtlasTextureData data = atlasData.get(spriteAtlasTexture);
            final Identifier id = spriteAtlasTexture.getId();
            final int glId = TextureUtil.generateId();
            final int width = data.width;
            final int maxLevel = data.maxLevel;
            final int height = data.height;
            TextureUtil.allocate(glId, maxLevel, width, height);
            ((AbstractTextureAccessor) spriteAtlasTexture).setGlId(glId);
            //ding dong lwjgl here are their styles
            ((SpriteAtlasTextureAccessor) spriteAtlasTexture).getSprites().forEach((identifier1, sprite) -> sprite.upload());
            //helu textures here are the atlases
            textureManager.registerTexture(id, spriteAtlasTexture);
            LOGGER.info("Allocated: {}x{}x{} {}-atlas", width, height, maxLevel, id);
        });
    }

    private <T> T deserialize(Class<T> clazz, Path path, String name) {
        try {
            LOGGER.info("  Starting " + name + " Deserialization.");
            //noinspection unchecked
            T out = (T) StreamInput.create(Files.newInputStream(path)).deserialize(serializers.get(clazz));
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

    private <T> void serializeObject(T clazz, Path path, String name) {
        try {
            task = "Serializing " + name;
            LOGGER.info("  Starting " + name + " Serialization.");
            StreamOutput output = StreamOutput.create(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            //noinspection unchecked
            output.serialize(serializers.get(clazz.getClass()), clazz);
            output.close();
            LOGGER.info("    Finished " + name + " Serialization.");
        } catch (IOException e) {
            LOGGER.fatal("Serializers: " + serializers.size());
            serializers.forEach((aClass, binarySerializer) -> LOGGER.fatal("Class: " + aClass + " Serializer: " + binarySerializer));
            e.printStackTrace();
        }
        tasksComplete++;
    }

    public void destroyCache(Exception exception) {
        if (!debug) {
            LOGGER.error("DashLoader failed, destroying cache and requesting recache. Slow start predicted.");
            exception.printStackTrace();
            if (!paths.values().stream().allMatch((path -> path.toFile().delete()))) {
                LOGGER.fatal("DashLoader removal failed, Closing because of corruption chance, please delete the cache manually or reinstall DashLoader.");
            }
        } else {
            exception.printStackTrace();
        }
    }

    public void destroyCache() {
        if (!debug) {
            LOGGER.error("DashLoader failed, destroying cache and requesting recache. Slow start predicted.");
            if (!paths.values().stream().allMatch((path -> path.toFile().delete()))) {
                LOGGER.fatal("DashLoader removal failed, Closing because of corruption chance, please delete the cache manually or reinstall DashLoader.");
            }
        }
    }

    private void logAndTask(String s) {
        LOGGER.info(s);
        tasksComplete++;
        task = s;
    }

    private File prepareAccess(File file) {
        if (!file.canWrite()) {
            file.setWritable(true);
        }
        if (!file.canRead()) {
            file.setReadable(true);
        }
        return file;
    }

    /**
     * <h1>Init below</h1>
     * Everything that gets called on launch here
     */
    private void initPaths() {
        paths.clear();
        paths.put(DashCachePaths.REGISTRY, config.resolve("quantumfusion/dashloader/registry.activej"));
        paths.put(DashCachePaths.MAPPINGS, config.resolve("quantumfusion/dashloader/mappings.activej"));
        paths.put(DashCachePaths.DASH_INFO, config.resolve("quantumfusion/dashloader/metadata.activej"));
    }

    private void addModelType(DashModelFactory factory) {
        modelMappings.put(factory.getModelType(), factory);
    }

    private void initModelMappings() {
        modelMappings.clear();
        addModelType(new DashBasicBakedModelFactory());
        addModelType(new DashBuiltInBakedModelFactory());
        addModelType(new DashMultipartBakedModelFactory());
        addModelType(new DashWeightedBakedModelFactory());
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> modContainer.getMetadata().getCustomValue("dashmodel"));
    }

    private void initSerializers() {
        LOGGER.info("Started Serializer Creation");
        serializers = new Object2ObjectOpenHashMap<>();
        List<Class<?>> modelTypes = new ArrayList<>();
        modelMappings.values().forEach(dashModel -> modelTypes.add(dashModel.getDashModelType()));

        HashMap<Class<?>,SerializerBuilder> serializerBuilders = new HashMap<>();
        serializerBuilders.put(DashRegistry.class,
                SerializerBuilder.create()
                        .withSubclasses("models", DashBasicBakedModel.class, DashWeightedBakedModel.class, DashMultipartBakedModel.class, DashBuiltinBakedModel.class)
                        .withSubclasses("fonts", DashBitmapFont.class, DashUnicodeFont.class, DashBlankFont.class)
                        .withSubclasses("predicates", DashAndPredicate.class, DashSimplePredicate.class, DashOrPredicate.class, DashStaticPredicate.class)
                        .withSubclasses("properties", DashBooleanProperty.class, DashEnumProperty.class, DashDirectionProperty.class, DashIntProperty.class)
                        .withSubclasses("values", DashBooleanValue.class, DashEnumValue.class, DashDirectionValue.class, DashIntValue.class)
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializerBuilders.put(MappingData.class, SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));
        serializerBuilders.put(DashLoaderInfo.class, SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));
        serializerBuilders.entrySet().parallelStream().forEach(entry -> serializers.put(entry.getKey(),entry.getValue().build(entry.getKey())));
        LOGGER.info("Created Serializers");
    }


}
