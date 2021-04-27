package net.quantumfusion.dashloader;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
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
import net.quantumfusion.dashloader.cache.atlas.DashExtraAtlasData;
import net.quantumfusion.dashloader.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dashloader.cache.atlas.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dashloader.cache.font.DashFontManagerData;
import net.quantumfusion.dashloader.cache.font.fonts.DashBitmapFont;
import net.quantumfusion.dashloader.cache.font.fonts.DashBlankFont;
import net.quantumfusion.dashloader.cache.font.fonts.DashUnicodeFont;
import net.quantumfusion.dashloader.cache.misc.DashLoaderInfo;
import net.quantumfusion.dashloader.cache.misc.DashParticleData;
import net.quantumfusion.dashloader.cache.models.*;
import net.quantumfusion.dashloader.cache.models.factory.*;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.mixin.*;
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
import java.util.concurrent.ConcurrentHashMap;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int totalTasks = 17;
    public static final double formatVersion = 0.3;
    public static final String version = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    private static final Path config = FabricLoader.getInstance().getConfigDir().normalize();
    public static String task = "Starting DashLoader";
    private static DashLoader instance;
    public int tasksComplete = 0;
    public DashCacheState state;



    public final HashMap<Class<? extends BakedModel>, DashModelFactory> modelMappings = new HashMap<>();

    public final HashMap<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    public final HashMap<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();

    private  Object2ObjectMap<Class<?>, BinarySerializer> serializers = new Object2ObjectOpenHashMap<>();
    private final List<SpriteAtlasTexture> atlasesToRegister;
    private final Map<DashCachePaths, Path> paths = new HashMap<>();
    private final List<SpriteAtlasTexture> extraAtlases;
    //output
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;
    public Map<Identifier, List<Sprite>> particlesOut;
    public Map<Identifier, List<Font>> fontsOut;
    public List<String> splashTextOut;
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
        logAndTask("Mapping Atlas");
        serializeObject(new DashSpriteAtlasManager(atlasManager, atlasData, registry), paths.get(DashCachePaths.ATLAS), "Atlas");

        logAndTask("Mapping Blockstates");
        serializeObject(new DashBlockStateData(stateLookup, registry), paths.get(DashCachePaths.BLOCKSTATES), "BlockState");

        logAndTask("Mapping Models");
        serializeObject(new DashModelData(models, multipartData, registry), paths.get(DashCachePaths.MODEL), "Model");

        logAndTask("Mapping Particles");
        serializeObject(new DashParticleData(particleSprites, particleAtlas, registry), paths.get(DashCachePaths.PARTICLE), "Particle");

        logAndTask("Mapping Fonts");

        Map<Identifier, List<Font>> fonts = new ConcurrentHashMap<>();
        ((FontManagerAccessor)((MinecraftClientAccessor)MinecraftClient.getInstance()).getFontManager()).getFontStorages().forEach((identifier, fontStorage) -> fonts.put(identifier, ((FontStorageAccessor)fontStorage).getFonts()));
        serializeObject(DashFontManagerData.toDash(fonts, registry), paths.get(DashCachePaths.FONT), "Font");

        logAndTask("Mapping Splash Text");
        serializeObject(new DashSplashTextData(splashText), paths.get(DashCachePaths.SPLASH_TEXT), "Splash Text");

        logAndTask("Mapping Extra Atlases");
        tasksComplete++;
        DashExtraAtlasData extraAtlasData = new DashExtraAtlasData();
        this.extraAtlases.forEach(spriteAtlasTexture -> extraAtlasData.addAtlas(spriteAtlasTexture, registry));
        serializeObject(extraAtlasData, paths.get(DashCachePaths.EXTRA_ATLAS), "Extra Atlas");
        serializeObject(registry, paths.get(DashCachePaths.REGISTRY), "Registry");
        task = "Caching is now complete.";
        LOGGER.info("Created cache in " + TimeHelper.getDecimalMs(start, Instant.now()) + "s");
    }

    public void loadDashCache() {
        LOGGER.info("Starting DashLoader Deserialization");
        state = DashCacheState.LOADING;
        try {
            DashRegistry registry = deserialize(DashRegistry.class, paths.get(DashCachePaths.REGISTRY), "Registry");
            LOGGER.info("      Loading Registry");
            registry.toUndash();


            DashSplashTextData splashTextOut = deserialize(DashSplashTextData.class, paths.get(DashCachePaths.SPLASH_TEXT), "Splash Text");
            LOGGER.info("      Loading Splash Text");
            this.splashTextOut = splashTextOut.splashList;


            DashSpriteAtlasManager atlas = deserialize(DashSpriteAtlasManager.class, paths.get(DashCachePaths.ATLAS), "BakedModel Atlas");
            LOGGER.info("      Loading BakedModel Atlas");
            atlasManagerOut = atlas.toUndash(registry);
            ((SpriteAtlasManagerAccessor) atlasManagerOut).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlasesToRegister.add(spriteAtlasTexture));


            DashBlockStateData blockStateData = deserialize(DashBlockStateData.class, paths.get(DashCachePaths.BLOCKSTATES), "BlockState");
            LOGGER.info("      Loading BlockState Data");
            stateLookupOut = blockStateData.toUndash(registry);


            DashParticleData particleData = deserialize(DashParticleData.class, paths.get(DashCachePaths.PARTICLE), "Particle");
            LOGGER.info("      Loading Particle Data");
            Pair<Map<Identifier, List<Sprite>>, SpriteAtlasTexture> outParticle = particleData.toUndash(registry);
            particlesOut = outParticle.getLeft();
            atlasesToRegister.add(outParticle.getValue());

            DashModelData modelData = deserialize(DashModelData.class, paths.get(DashCachePaths.MODEL), "Model");
            LOGGER.info("    Loading Model Data");
            modelsOut = modelData.toUndash(registry);

            DashExtraAtlasData extraAtlasdata = deserialize(DashExtraAtlasData.class, paths.get(DashCachePaths.EXTRA_ATLAS), "Extra Atlas");
            LOGGER.info("      Loading Extra Atlas Data");
            atlasesToRegister.addAll(extraAtlasdata.toUndash(registry));

            DashFontManagerData fontData = deserialize(DashFontManagerData.class, paths.get(DashCachePaths.FONT), "Font");
            fontsOut = fontData.toUndash(registry);
            LOGGER.info("    Loading Model Data");



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
            final int glId = TextureUtil.generateId();
            final Identifier id = spriteAtlasTexture.getId();
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

    private static final boolean debug = true;

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
        paths.put(DashCachePaths.BLOCKSTATES, config.resolve("quantumfusion/dashloader/blockstate-mappings.activej"));
        paths.put(DashCachePaths.MODEL, config.resolve("quantumfusion/dashloader/model-mappings.activej"));
        paths.put(DashCachePaths.ATLAS, config.resolve("quantumfusion/dashloader/atlas-mappings.activej"));
        paths.put(DashCachePaths.PARTICLE, config.resolve("quantumfusion/dashloader/particle-mappings.activej"));
        paths.put(DashCachePaths.FONT, config.resolve("quantumfusion/dashloader/font-mappings.activej"));
        paths.put(DashCachePaths.EXTRA_ATLAS, config.resolve("quantumfusion/dashloader/extraatlas-mappings.activej"));
        paths.put(DashCachePaths.SPLASH_TEXT, config.resolve("quantumfusion/dashloader/splash-text.activej"));
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
        ArrayList<Class<?>> list = new ArrayList<>();
        Object2ObjectMap<Class<?>, SerializerBuilder> serializersOut = new Object2ObjectOpenHashMap<>();
        modelMappings.values().forEach(dashModel -> list.add(dashModel.getClass()));
        serializersOut.put(DashRegistry.class,
                SerializerBuilder.create()
                        .withSubclasses("models", DashBasicBakedModel.class, DashBuiltinBakedModel.class, DashMultipartBakedModel.class, DashWeightedBakedModel.class)
                        .withSubclasses("fonts", DashBitmapFont.class, DashUnicodeFont.class, DashBlankFont.class)
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashModelData.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashSpriteAtlasManager.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashBlockStateData.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));


        serializersOut.put(DashParticleData.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashExtraAtlasData.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashFontManagerData.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashSplashTextData.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.put(DashLoaderInfo.class,
                SerializerBuilder.create().withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE));

        serializersOut.entrySet().parallelStream().forEach(classSerializerBuilderEntry -> {
            final Class<?> key = classSerializerBuilderEntry.getKey();
            serializers.put(key,classSerializerBuilderEntry.getValue().build(key));
        });
        while (serializersOut.size() > serializers.size()) { }
        LOGGER.info("Created Serializers");
    }


}
