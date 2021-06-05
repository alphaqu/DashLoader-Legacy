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
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.api.DashLoaderAPI;
import net.quantumfusion.dashloader.blockstate.DashBlockStateData;
import net.quantumfusion.dashloader.data.registry.*;
import net.quantumfusion.dashloader.font.DashFontManagerData;
import net.quantumfusion.dashloader.image.DashSpriteAtlasData;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.misc.DashMetadata;
import net.quantumfusion.dashloader.misc.DashParticleData;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.mixin.accessor.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.model.DashModelData;
import net.quantumfusion.dashloader.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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

    // TODO: config
    public static final int totalTasks = 22;
    public static final double formatVersion = 2;

    public static final String VERSION;
    public static final Path CONFIG;
    private static final boolean IS_DEV;

    public static ForkJoinPool THREAD_POOL;
    public static String task = "Starting DashLoader";
    public final Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    public final Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();
    public final List<SpriteAtlasTexture> atlasesToRegister;
    private final ClassLoader classLoader;
    private final List<SpriteAtlasTexture> extraAtlases;

    @SuppressWarnings("rawtypes")
    private final ConcurrentHashMap<Class<?>, BinarySerializer> serializers = new ConcurrentHashMap<>();

    public int tasksComplete = 0;
    public DashCacheState state;
    public Map<Identifier, List<Font>> fonts = new HashMap<>();

    public final DashLoaderAPI api;
    //output

    private MappingData mappings = new MappingData();
    private SpriteAtlasManager atlasManager;
    private Object2IntMap<BlockState> stateLookup;
    private Object2IntMap<BlockState> stateLookupOut;

    private Map<Identifier, BakedModel> models;
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> particleSprites;
    private SpriteAtlasTexture particleAtlas;
    private List<String> splashText;

    private static DashLoader instance;

    public DashLoader(ClassLoader classLoader) {
        LOGGER.info("Creating DashLoader Instance");
        instance = this;
        this.classLoader = classLoader;
        this.extraAtlases = new ArrayList<>();
        this.atlasesToRegister = new ArrayList<>();
        this.api = new DashLoaderAPI();
        LOGGER.info("Created DashLoader with classloader: " + classLoader.getClass().getSimpleName());
    }

    static {
        var loader = FabricLoader.getInstance();
        var dashModContainer = loader.getModContainer("dashloader");
        if (dashModContainer.isEmpty()) {
            throw new DashException("cannot find DashLoader in the mod registry!");
        }
        VERSION = dashModContainer.get().getMetadata().getVersion().getFriendlyString();
        CONFIG = loader.getConfigDir().normalize();
        IS_DEV = loader.isDevelopmentEnvironment();
    }

    public static DashLoader getInstance() {
        return instance;
    }

    // TODO: are these really necessary?
    public SpriteAtlasManager getAtlasManagerOut() { return mappings.atlasManagerOut; }
    public Object2IntMap<BlockState> getStateLookupOut() { return stateLookupOut; }
    public Map<Identifier, BakedModel> getModelsOut() { return mappings.modelsOut; }
    public Map<Identifier, List<Sprite>> getParticlesOut() { return mappings.particlesOut; }
    public Map<Identifier, List<Font>> getFontsOut() { return mappings.fontsOut; }
    public List<String> getSplashTextOut() { return mappings.splashTextOut; }

    public void reload() {
        LOGGER.info("Starting DashLoader thread.");
        if (IS_DEV) {
            LOGGER.warn("DashLoader launched in dev.");
        }
        state = DashCacheState.LOADING;

        Instant start = Instant.now();
        //Thread dashLoaderThread = new Thread(this::dashLoaderThread);
        dashLoaderThread();
        LOGGER.info("Loaded cache in {}s", TimeHelper.getDecimalSeconds(start, Instant.now()));

        //dashLoaderThread.setContextClassLoader(classLoader);
        //dashLoaderThread.setName("dashloader-supervisor");
        //dashLoaderThread.start();
    }

    private void dashLoaderThread() {
        initThreadPool();
        api.initAPI();
        initSerializers();
        createDirectory();
        LOGGER.info("[4/4] Launching DashCache.");
        var metadata = DashMetadata.create();
        var shouldReload = shouldReload(metadata);

        if (shouldReload == ReloadEnum.ACCEPT) {
            loadDashCache();
        } else {
            switch (shouldReload) {
                case FORMAT_CHANGE -> LOGGER.warn("DashLoader update changed format. Recache requested.");
                case MOD_CHANGE -> LOGGER.warn("DashLoader detected mod change. Recache requested.");
                case MISSING_FILES -> LOGGER.warn("DashLoader detected missing files. Recache requested.");
            }
            destroyCache();
            createMetadata(metadata);
            state = DashCacheState.EMPTY;
        }
        THREAD_POOL.shutdown();
    }

    private ReloadEnum shouldReload(DashMetadata metadata) {
        if (Arrays.stream(DashCachePaths.values()).allMatch(path -> Files.exists(path.getPath()))) {
            DashMetadata previousMetadata = deserialize(DashMetadata.class, DashCachePaths.DASH_METADATA.getPath(), "Metadata");
            return metadata.getState(previousMetadata);
        } else {
            return ReloadEnum.MISSING_FILES;
        }
    }

    private void createMetadata(DashMetadata data) {
        tasksComplete--;
        serializeObject(data, DashCachePaths.DASH_METADATA.getPath(), "Mod Info");
    }

    private void createDirectory() {
        // we have to use old IO here unfortunately, since NIO doesn't have a very nice interface for setting
        // permissions portably - leocth

        var configDir = CONFIG.resolve("quantumfusion/dashloader").toFile();
        if (!configDir.setWritable(true) && configDir.setReadable(true)) {
            throw new DashException("Failed to prepare access for cache directory (" + configDir.getPath() + ")! Please check if you have the right permissions!");
        }
        if (!configDir.mkdirs()) {
            throw new DashException("Cannot create directory at " + configDir.getPath() + "!");
        }
    }


    public void addExtraAtlasAssets(SpriteAtlasTexture atlas) {
        extraAtlases.add(atlas);
    }

    public void setBakedModelAssets(SpriteAtlasManager atlasManager,
                                    Object2IntMap<BlockState> stateLookup,
                                    Map<Identifier, BakedModel> models) {
        this.atlasManager = atlasManager;
        this.models = models;
        this.stateLookup = stateLookup;
    }

    public void setParticleManagerAssets(Map<Identifier, ParticleManager.SimpleSpriteProvider> particles, SpriteAtlasTexture atlas) {
        this.particleSprites = particles;
        this.particleAtlas = atlas;
    }

    public void setSplashTextAssets(List<String> splashText) {
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

        serializeObject(registry.makeBlockStatesData(), DashCachePaths.REGISTRY_BLOCKSTATE.getPath(), "Registry Blockstates");
        serializeObject(registry.makeFontsData(), DashCachePaths.REGISTRY_FONT.getPath(), "Registry Fonts");
        serializeObject(registry.makeIdentifiersData(), DashCachePaths.REGISTRY_IDENTIFIER.getPath(), "Registry Identifiers");
        serializeObject(registry.makeImagesData(), DashCachePaths.REGISTRY_IMAGE.getPath(), "Registry Images");
        serializeObject(registry.makeModelsData(), DashCachePaths.REGISTRY_MODEL.getPath(), "Registry Models");
        serializeObject(registry.makePredicatesData(), DashCachePaths.REGISTRY_PREDICATE.getPath(), "Registry Predicates");
        serializeObject(registry.makePropertiesData(), DashCachePaths.REGISTRY_PROPERTY.getPath(), "Registry Properties");
        serializeObject(registry.makePropertyValuesData(), DashCachePaths.REGISTRY_PROPERTYVALUE.getPath(), "Registry PropertyValues");
        serializeObject(registry.makeSpritesData(), DashCachePaths.REGISTRY_SPRITE.getPath(), "Registry Sprites");
        registry.apiReport(LOGGER);
        THREAD_POOL.shutdown();
        task = "Caching is now complete.";
        LOGGER.info("Created cache in {}s", TimeHelper.getDecimalSeconds(start, Instant.now()));
    }

    private void initThreadPool() {
        THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 2, pool -> {
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
            // TODO: maybe we can do it without reassigning the fields
            ThreadHelper.exec(
                () -> registry.blockStates = deserialize(RegistryBlockStateData.class, DashCachePaths.REGISTRY_BLOCKSTATE.getPath(), "Registry BlockStates").blockStates,
                () -> registry.fonts = deserialize(RegistryFontData.class, DashCachePaths.REGISTRY_FONT.getPath(), "Registry Fonts").fonts,
                () -> registry.identifiers = deserialize(RegistryIdentifierData.class, DashCachePaths.REGISTRY_IDENTIFIER.getPath(), "Registry Identifiers").identifiers,
                () -> registry.images = deserialize(RegistryImageData.class, DashCachePaths.REGISTRY_IMAGE.getPath(), "Registry Images").images,
                () -> registry.modelsToDeserialize = deserialize(RegistryModelData.class, DashCachePaths.REGISTRY_MODEL.getPath(), "Registry Models").models,
                () -> registry.predicates = deserialize(RegistryPredicateData.class, DashCachePaths.REGISTRY_PREDICATE.getPath(), "Registry Predicates").predicates,
                () -> registry.properties = deserialize(RegistryPropertyData.class, DashCachePaths.REGISTRY_PROPERTY.getPath(), "Registry Properties").property,
                () -> registry.propertyValues = deserialize(RegistryPropertyValueData.class, DashCachePaths.REGISTRY_PROPERTYVALUE.getPath(), "Registry PropertyValues").propertyValues,
                () -> registry.sprites = deserialize(RegistrySpriteData.class, DashCachePaths.REGISTRY_SPRITE.getPath(), "Registry Sprites").sprites
            );
            LOGGER.info("\tLoading Registry");
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
            LOGGER.info("\tLoading Mappings");
            atlasesToRegister.addAll(mappings.toUndash(registry));

            LOGGER.info("\tLoaded DashLoader");
            stateLookupOut = mappings.stateLookupOut;
            state = DashCacheState.LOADED;
        } catch (Exception e) {
            destroyCache(e);
            state = DashCacheState.CRASHLOADER;
        }
    }

    public void applyDashCache(TextureManager textureManager, Profiler profiler) {
        //register textures
        profiler.push("atlas");
        atlasesToRegister.forEach(spriteAtlasTexture -> {
            //atlas registration
            final var data = atlasData.get(spriteAtlasTexture);
            final var id = spriteAtlasTexture.getId();
            final var glId = TextureUtil.generateTextureId();
            final var width = data.width;
            final var maxLevel = data.maxLevel;
            final var height = data.height;
            TextureUtil.prepareImage(glId, maxLevel, width, height);

            ((AbstractTextureAccessor) spriteAtlasTexture).setGlId(glId);
            //ding dong lwjgl here are their styles
            ((SpriteAtlasTextureAccessor) spriteAtlasTexture).getSprites().forEach((identifier1, sprite) -> {
                final SpriteAccessor access = (SpriteAccessor) sprite;
                access.setAtlas(spriteAtlasTexture);
                access.setId(identifier1);
                sprite.upload();
            });
            //helu textures here are the atlases

            textureManager.registerTexture(id, spriteAtlasTexture);
            spriteAtlasTexture.setFilter(false, maxLevel > 0);
            LOGGER.info("Allocated: {}x{}x{} {}-atlas", width, height, maxLevel, id);
        });
        profiler.swap("baking");
        profiler.pop();
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
                throw new DashException(name + " Deserialization failed: cannot create input file stream");
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
            LOGGER.info("\tStarting {} Serialization.", name);
            StreamOutput output = StreamOutput.create(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            //noinspection unchecked
            output.serialize(serializers.get(clazz.getClass()), clazz);
            output.close();
            LOGGER.info("\tFinished {} Serialization.", name);
        } catch (IOException e) {
            LOGGER.fatal("Serializers: {}", serializers.size());
            serializers.forEach((klazz, binarySerializer) -> LOGGER.fatal("Class: {}, Serializer: {}", klazz, binarySerializer));
            e.printStackTrace();
        }
        tasksComplete++;
    }

    public void destroyCache(Exception exception) {
        if (!IS_DEV) {
            LOGGER.error("DashLoader failed, destroying cache and requesting recache. Slow start predicted.");
            exception.printStackTrace();

            // TODO: use Files.deleteIfExists if we can somehow use it without doing error handling
            if (!Arrays.stream(DashCachePaths.values()).allMatch((path -> !path.getPath().toFile().exists() || path.getPath().toFile().delete()))) {
                LOGGER.fatal("DashLoader file removal failed. Something went terribly wrong ");
            }
        } else {
            exception.printStackTrace();
        }
    }

    public void destroyCache() {
        if (!IS_DEV) {
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

    /**
     * <h1>E gamin - its the game</h1>
     * fsda
     */
    private void initSerializers() {
        LOGGER.info("[3/4]  Started Serializer init.");
        final var start = Instant.now();
        // it has to be in an array, since loading classes in a thread pool apparently triggers a CME,
        // or so as alpha puts it - leocth
        final Class<?>[] serializerClassesForInit = {
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
                RegistrySpriteData.class
        };
        final Runnable[] runnables = {
                () -> addSerializer(serializerClassesForInit[0], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[1], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[2], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[3], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[4], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[5], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[6], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[7], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[8], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[9], SerializerBuilder.create()),
                () -> addSerializer(serializerClassesForInit[10], SerializerBuilder.create().withSubclasses("fonts", api.fontTypes)),
                () -> addSerializer(serializerClassesForInit[11], SerializerBuilder.create().withSubclasses("models", api.modelTypes)),
                () -> addSerializer(serializerClassesForInit[12], SerializerBuilder.create().withSubclasses("predicates", api.predicateTypes)),
                () -> addSerializer(serializerClassesForInit[13], SerializerBuilder.create().withSubclasses("properties", api.propertyTypes)),
                () -> addSerializer(serializerClassesForInit[14], SerializerBuilder.create().withSubclasses("values", api.propertyValueTypes)),
                () -> addSerializer(serializerClassesForInit[15], SerializerBuilder.create())
        };
        ThreadHelper.exec(runnables);
        LOGGER.info("[3/4] [{}ms] Created Serializers.", Duration.between(start, Instant.now()).toMillis());
    }

    private void addSerializer(Class<?> clazz, SerializerBuilder builder) {
        serializers.put(clazz, builder.withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE).build(clazz));
    }
}
