package net.quantumfusion.dashloader;

import com.mojang.blaze3d.platform.TextureUtil;
import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.api.DashLoaderAPI;
import net.quantumfusion.dashloader.atlas.DashSpriteAtlasData;
import net.quantumfusion.dashloader.atlas.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.blockstates.DashBlockStateData;
import net.quantumfusion.dashloader.font.DashFontManagerData;
import net.quantumfusion.dashloader.misc.DashMetadata;
import net.quantumfusion.dashloader.misc.DashParticleData;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.mixin.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.models.DashModelData;
import net.quantumfusion.dashloader.registry.*;
import net.quantumfusion.dashloader.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int totalTasks = 22;
    public static final double formatVersion = 2;
    public static final String version = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    private static final Path config = FabricLoader.getInstance().getConfigDir().normalize();
    private static final boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static ForkJoinPool THREADPOOL;
    public static String task = "Starting DashLoader";
    private static DashLoader instance;
    public final Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    public final Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();
    public final List<SpriteAtlasTexture> atlasesToRegister;
    private final ClassLoader classLoader;
    private final List<SpriteAtlasTexture> extraAtlases;
    private final ConcurrentHashMap<Class<?>, BinarySerializer> serializers = new ConcurrentHashMap<>();
    public int tasksComplete = 0;
    public DashCacheState state;
    public Map<Identifier, List<Font>> fonts = new HashMap<>();
    private DashLoaderAPI api;
    //output
    private Object2IntMap<BlockState> stateLookupOut;
    private MappingData mappings = new MappingData();
    private SpriteAtlasManager atlasManager;
    private Object2IntMap<BlockState> stateLookup;
    private Map<Identifier, BakedModel> models;
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> particleSprites;
    private SpriteAtlasTexture particleAtlas;
    private List<String> splashText;

    public DashLoader(ClassLoader classLoader) {
        LOGGER.info("Creating DashLoader Instance");
        instance = this;
        this.classLoader = classLoader;
        extraAtlases = new ArrayList<>();
        atlasesToRegister = new ArrayList<>();
        api = new DashLoaderAPI();
        LOGGER.info("Created DashLoader with classloader: " + classLoader.getClass().getSimpleName());
    }

    public static Path getConfig() {
        return config;
    }

    public static DashLoader getInstance() {
        return instance;
    }

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

    public void reload() {
        LOGGER.info("Starting DashLoader thread.");
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.warn("DashLoader launched in dev.");
        }
        state = DashCacheState.LOADING;
        Instant start = Instant.now();
        Thread dashLoaderThread = new Thread(() -> {
            initThreadPool();
            api.initAPI();
            initSerializers();
            createDirectory();
            LOGGER.info("[4/4] Launching DashCache.");
            DashMetadata currentMetadata = DashMetadata.create();
            ReloadEnum shouldReload;
            if (Arrays.stream(DashCachePaths.values()).allMatch((path) -> path.getPath().toFile().exists())) {
                DashMetadata previousMetadata = deserialize(DashMetadata.class, DashCachePaths.DASH_METADATA.getPath(), "Metadata");
                shouldReload = currentMetadata.getState(previousMetadata);
            } else {
                shouldReload = ReloadEnum.MISSING_FILES;
            }
            if (shouldReload == ReloadEnum.ACCEPT) {
                loadDashCache();
            } else {
                switch (shouldReload) {
                    case FORMAT_CHANGE:
                        LOGGER.warn("DashLoader update changed format. Recache requested.");
                        break;
                    case MOD_CHANGE:
                        LOGGER.warn("DashLoader detected mod change. Recache requested.");
                        break;
                    case MISSING_FILES:
                        LOGGER.warn("DashLoader detected missing files. Recache requested.");
                        break;
                }
                destroyCache();
                createMetadata(currentMetadata);
                state = DashCacheState.EMPTY;
            }
            shutdownThreadPool();
            LOGGER.info("Loaded cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
        });
        dashLoaderThread.setContextClassLoader(classLoader);
        dashLoaderThread.setName("dashloader-supervisor");
        dashLoaderThread.start();
    }


    public DashLoaderAPI getApi() {
        return api;
    }

    private void shutdownThreadPool() {
        THREADPOOL.shutdown();
    }

    private void createMetadata(DashMetadata data) {
        tasksComplete--;
        serializeObject(data, DashCachePaths.DASH_METADATA.getPath(), "Mod Info");
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
        initThreadPool();
        api.initAPI();
        initSerializers();
        createDirectory();
        tasksComplete++;
        DashRegistry registry = new DashRegistry(this);
        MappingData mappings = new MappingData();

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

        logAndTask("Mapping Atlas");
        mappings.setSpriteAtlasData(new DashSpriteAtlasData(atlasManager, atlasData, registry, extraAtlases));

        serializeObject(mappings.modelData, DashCachePaths.MODELS.getPath(), "Models");
        serializeObject(mappings.spriteAtlasData, DashCachePaths.SPRITEATLAS.getPath(), "Atlases");
        serializeObject(mappings.blockStateData, DashCachePaths.BLOCKSTATE.getPath(), "Blockstates");
        serializeObject(mappings.particleData, DashCachePaths.PARTICLE.getPath(), "Particles");
        serializeObject(mappings.fontManagerData, DashCachePaths.FONT.getPath(), "Fonts");
        serializeObject(mappings.splashTextData, DashCachePaths.SPLASH.getPath(), "Splash Text");

        serializeObject(registry.getBlockstates(), DashCachePaths.REGISTRY_BLOCKSTATE.getPath(), "Registry Blockstates");
        serializeObject(registry.getFonts(), DashCachePaths.REGISTRY_FONT.getPath(), "Registry Fonts");
        serializeObject(registry.getIdentifiers(), DashCachePaths.REGISTRY_IDENTIFIER.getPath(), "Registry Identifiers");
        serializeObject(registry.getImages(), DashCachePaths.REGISTRY_IMAGE.getPath(), "Registry Images");
        serializeObject(registry.getModels(), DashCachePaths.REGISTRY_MODEL.getPath(), "Registry Models");
        serializeObject(registry.getPredicates(), DashCachePaths.REGISTRY_PREDICATE.getPath(), "Registry Predicates");
        serializeObject(registry.getProperties(), DashCachePaths.REGISTRY_PROPERTY.getPath(), "Registry Properties");
        serializeObject(registry.getPropertyValues(), DashCachePaths.REGISTRY_PROPERTYVALUE.getPath(), "Registry PropertyValues");
        serializeObject(registry.getSprites(), DashCachePaths.REGISTRY_SPRITE.getPath(), "Registry Sprites");
        registry.apiReport(LOGGER);
        shutdownThreadPool();
        task = "Caching is now complete.";
        LOGGER.info("Created cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
    }

    private void initThreadPool() {
        THREADPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 2, pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("dashloader-thread-" + worker.getPoolIndex());
            worker.setContextClassLoader(classLoader);
            return worker;
        }, null, true);
    }

    public void loadDashCache() {
        LOGGER.info("Starting DashLoader Deserialization");
        try {
            DashRegistry registry = new DashRegistry(this);
            ThreadHelper.exec(
                    () -> registry.setBlockstates(deserialize(RegistryBlockStateData.class, DashCachePaths.REGISTRY_BLOCKSTATE.getPath(), "Registry Blockstates").blockstates),
                    () -> registry.setFonts(deserialize(RegistryFontData.class, DashCachePaths.REGISTRY_FONT.getPath(), "Registry Fonts").fonts),
                    () -> registry.setIdentifiers(deserialize(RegistryIdentifierData.class, DashCachePaths.REGISTRY_IDENTIFIER.getPath(), "Registry Identifiers").identifiers),
                    () -> registry.setImages(deserialize(RegistryImageData.class, DashCachePaths.REGISTRY_IMAGE.getPath(), "Registry Images").images),
                    () -> registry.setModels(deserialize(RegistryModelData.class, DashCachePaths.REGISTRY_MODEL.getPath(), "Registry Models")),
                    () -> registry.setPredicates(deserialize(RegistryPredicateData.class, DashCachePaths.REGISTRY_PREDICATE.getPath(), "Registry Predicates").predicates),
                    () -> registry.setProperties(deserialize(RegistryPropertyData.class, DashCachePaths.REGISTRY_PROPERTY.getPath(), "Registry Properties").property),
                    () -> registry.setPropertyValues(deserialize(RegistryPropertyValueData.class, DashCachePaths.REGISTRY_PROPERTYVALUE.getPath(), "Registry PropertyValues").propertyValues),
                    () -> registry.setSprites(deserialize(RegistrySpriteData.class, DashCachePaths.REGISTRY_SPRITE.getPath(), "Registry Sprites").sprites)
            );
            LOGGER.info("      Loading Registry");
            registry.toUndash();

            mappings = new MappingData();
            ThreadHelper.exec(
                    () -> mappings.setModelData(deserialize(DashModelData.class, DashCachePaths.MODELS.getPath(), "Models")),
                    () -> mappings.setSpriteAtlasData(deserialize(DashSpriteAtlasData.class, DashCachePaths.SPRITEATLAS.getPath(), "Atlases")),
                    () -> mappings.setBlockStateData(deserialize(DashBlockStateData.class, DashCachePaths.BLOCKSTATE.getPath(), "Blockstates")),
                    () -> mappings.setParticleData(deserialize(DashParticleData.class, DashCachePaths.PARTICLE.getPath(), "Particles")),
                    () -> mappings.setFontManagerData(deserialize(DashFontManagerData.class, DashCachePaths.FONT.getPath(), "Fonts")),
                    () -> mappings.setSplashTextData(deserialize(DashSplashTextData.class, DashCachePaths.SPLASH.getPath(), "Splash Text"))
            );
            LOGGER.info("      Loading Mappings");
            atlasesToRegister.addAll(mappings.toUndash(registry));

            LOGGER.info("    Loaded DashLoader");
            stateLookupOut = mappings.stateLookupOut;
            state = DashCacheState.LOADED;
        } catch (Exception e) {
            destroyCache(e);
            state = DashCacheState.CRASHLOADER;
        }
    }

    public void applyDashCache(TextureManager textureManager) {
        //register textures
        System.out.println(atlasesToRegister.size());
        atlasesToRegister.forEach((spriteAtlasTexture) -> {
            //atlas registration
            final DashSpriteAtlasTextureData data = atlasData.get(spriteAtlasTexture);
            final Identifier id = spriteAtlasTexture.getId();
            final int glId = TextureUtil.generateTextureId();
            final int width = data.width;
            final int maxLevel = data.maxLevel;
            final int height = data.height;
            TextureUtil.prepareImage(glId, data.maxLevel, data.width, data.height);
            ((AbstractTextureAccessor) spriteAtlasTexture).setGlId(glId);
            //ding dong lwjgl here are their styles
            ((SpriteAtlasTextureAccessor) spriteAtlasTexture).getSprites().forEach((identifier1, sprite) -> sprite.upload());
            //helu textures here are the atlases
            textureManager.registerTexture(id, spriteAtlasTexture);
            textureManager.bindTexture(id);
            spriteAtlasTexture.setFilter(false, maxLevel > 0);
            LOGGER.info("Allocated: {}x{}x{} {}-atlas", width, height, maxLevel, id);
        });
    }

    @NotNull
    private <T> T deserialize(Class<T> clazz, Path path, String name) {
        try {
            //noinspection unchecked
            final BinarySerializer<T> serializer = serializers.get(clazz);
            if (serializer == null) {
                throw new DashException(name + " Serializer not found.");
            }
            T out = StreamInput.create(Files.newInputStream(path)).deserialize(serializer);
            if (out == null) {
                throw new DashException(name + " Deserialization failed");
            }
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new DashException(name + " File failed");
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
            if (!Arrays.stream(DashCachePaths.values()).allMatch((path -> !path.getPath().toFile().exists() || path.getPath().toFile().delete()))) {
                LOGGER.fatal("DashLoader file removal failed. Something went terribly wrong ");
            }
        } else {
            exception.printStackTrace();
        }
    }

    public void destroyCache() {
        if (!debug) {
            LOGGER.error("DashLoader failed, destroying cache and requesting recache. Slow start predicted.");
            if (!Arrays.stream(DashCachePaths.values()).allMatch((path -> !path.getPath().toFile().exists() || path.getPath().toFile().delete()))) {
                LOGGER.fatal("DashLoader file removal failed. Something went terribly wrong ");
            }
        }
    }

    private void logAndTask(String s) {
        LOGGER.info(s);
        tasksComplete++;
        task = s;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
     * <h1>E gamin - its the game</h1>
     * fsda
     */
    private void initSerializers() {
        LOGGER.info("[3/4]  Started Serializer init.");
        Instant start = Instant.now();
        final Class[] classes = new Class[]{
                DashMetadata.class,
                DashModelData.class,
                DashSpriteAtlasData.class,
                DashBlockStateData.class,
                DashParticleData.class,
                DashFontManagerData.class,
                DashSplashTextData.class,
                RegistryBlockStateData.class,
                RegistryIdentifierData.class,
                RegistryImageData.class,
                RegistryFontData.class,
                RegistryModelData.class,
                RegistryPredicateData.class,
                RegistryPropertyData.class,
                RegistryPropertyValueData.class,
                RegistrySpriteData.class};
        final Runnable[] runnables = {
                () -> addSerializer(classes[0], SerializerBuilder.create()),
                () -> addSerializer(classes[1], SerializerBuilder.create()),
                () -> addSerializer(classes[2], SerializerBuilder.create()),
                () -> addSerializer(classes[3], SerializerBuilder.create()),
                () -> addSerializer(classes[4], SerializerBuilder.create()),
                () -> addSerializer(classes[5], SerializerBuilder.create()),
                () -> addSerializer(classes[6], SerializerBuilder.create()),
                () -> addSerializer(classes[7], SerializerBuilder.create()),
                () -> addSerializer(classes[8], SerializerBuilder.create()),
                () -> addSerializer(classes[9], SerializerBuilder.create()),
                () -> addSerializer(classes[10], SerializerBuilder.create().withSubclasses("fonts", api.fontTypes)),
                () -> addSerializer(classes[11], SerializerBuilder.create().withSubclasses("models", api.modelTypes)),
                () -> addSerializer(classes[12], SerializerBuilder.create().withSubclasses("predicates", api.predicateTypes)),
                () -> addSerializer(classes[13], SerializerBuilder.create().withSubclasses("properties", api.propertyTypes)),
                () -> addSerializer(classes[14], SerializerBuilder.create().withSubclasses("values", api.propertyValueTypes)),
                () -> addSerializer(classes[15], SerializerBuilder.create())
        };
        ThreadHelper.exec(runnables);
        LOGGER.info("[3/4] [" + Duration.between(start, Instant.now()).toMillis() + "ms] Created Serializers.");
    }

    private void addSerializer(Class<?> clazz, SerializerBuilder builder) {
        serializers.put(clazz, builder.withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE).build(clazz));
    }


}
