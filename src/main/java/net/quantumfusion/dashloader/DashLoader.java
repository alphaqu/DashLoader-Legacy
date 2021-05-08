package net.quantumfusion.dashloader;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.launch.knot.Knot;
import net.gudenau.lib.unsafe.Unsafe;
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
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.api.models.*;
import net.quantumfusion.dashloader.api.properties.*;
import net.quantumfusion.dashloader.atlas.DashSpriteAtlasData;
import net.quantumfusion.dashloader.atlas.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.blockstates.DashBlockStateData;
import net.quantumfusion.dashloader.font.DashFontManagerData;
import net.quantumfusion.dashloader.font.fonts.DashBitmapFont;
import net.quantumfusion.dashloader.font.fonts.DashBlankFont;
import net.quantumfusion.dashloader.font.fonts.DashUnicodeFont;
import net.quantumfusion.dashloader.misc.DashMetadata;
import net.quantumfusion.dashloader.misc.DashParticleData;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.mixin.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.models.DashModelData;
import net.quantumfusion.dashloader.models.predicates.DashAndPredicate;
import net.quantumfusion.dashloader.models.predicates.DashOrPredicate;
import net.quantumfusion.dashloader.models.predicates.DashSimplePredicate;
import net.quantumfusion.dashloader.models.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.registry.*;
import net.quantumfusion.dashloader.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
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
    public static final double formatVersion = 0.7;
    public static final String version = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    private static final Path config = FabricLoader.getInstance().getConfigDir().normalize();
    private static final boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static ForkJoinPool THREADPOOL;
    public static String task = "Starting DashLoader";
    private static DashLoader instance;
    public final Map<Class<? extends BakedModel>, DashModelFactory> modelMappings = new ConcurrentHashMap<>();
    public final Map<Class<? extends Property<?>>, DashPropertyFactory> propertyMappings = new ConcurrentHashMap<>();
    public final Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    public final Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();
    public final List<SpriteAtlasTexture> atlasesToRegister;
    private final List<SpriteAtlasTexture> extraAtlases;
    public int tasksComplete = 0;
    public DashCacheState state;
    public Map<Identifier, List<Font>> fonts = new HashMap<>();
    //output
    private Object2IntMap<BlockState> stateLookupOut;
    private MappingData mappings = new MappingData();
    private ConcurrentHashMap<Class<?>, BinarySerializer> serializers = new ConcurrentHashMap<>();
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
            initAPI();
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
        dashLoaderThread.setName("dashloader-supervisor");
        dashLoaderThread.start();
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
        initAPI();
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
        shutdownThreadPool();
        task = "Caching is now complete.";
        LOGGER.info("Created cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
    }

    private void initThreadPool() {
        THREADPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 2, pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("dashloader-thread-" + worker.getPoolIndex());
            worker.setContextClassLoader(Knot.getLauncher().getTargetClassLoader());
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
                    () -> registry.setProperties(deserialize(RegistryPropertyData.class, DashCachePaths.REGISTRY_PROPERTY.getPath(), "Registry Properties").properties),
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

    @NotNull
    private <T> T deserialize(Class<T> clazz, Path path, String name) {
        try {
            //noinspection unchecked
            T out = (T) StreamInput.create(new BufferedInputStream(Files.newInputStream(path))).deserialize(serializers.get(clazz));
            if (out == null) {
                throw new DashException(name + " Deserialization failed");
            }
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

    private File prepareAccess(File file) {
        if (!file.canWrite()) {
            file.setWritable(true);
        }
        if (!file.canRead()) {
            file.setReadable(true);
        }
        return file;
    }


    private void addModelType(DashModelFactory factory) {
        modelMappings.put(factory.getModelType(), factory);
    }

    private void addPropertyType(DashPropertyFactory factory) {
        propertyMappings.put((Class<? extends Property<?>>) factory.getPropertyType(), factory);
    }

    private void initAPI() {
        Instant start = Instant.now();
        propertyMappings.clear();
        addPropertyType(new DashBooleanPropertyFactory());
        addPropertyType(new DashDirectionPropertyFactory());
        addPropertyType(new DashIntPropertyFactory());
        addPropertyType(new DashEnumPropertyFactory());
        modelMappings.clear();
        addModelType(new DashBasicBakedModelFactory());
        addModelType(new DashBuiltInBakedModelFactory());
        addModelType(new DashMultipartBakedModelFactory());
        addModelType(new DashWeightedBakedModelFactory());
        FabricLoader.getInstance().getAllMods().parallelStream().forEach(modContainer -> {
            final ModMetadata metadata = modContainer.getMetadata();
            final CustomValue dashModelValue = metadata.getCustomValue("dashloader:customfactory");
            if (dashModelValue != null) {
                try {
                    final CustomValue.CvArray values = dashModelValue.getAsArray();
                    for (CustomValue value : values) {
                        DashModelFactory factory = (DashModelFactory) Unsafe.allocateInstance(Class.forName(value.getAsString()));
                        addModelType(factory);
                        if (!metadata.getId().equals("dashloader"))
                            LOGGER.info("Added custom model: " + factory.getModelType().getSimpleName());
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("ModelFactory: " + dashModelValue.getAsString() + " not found. MOD: " + metadata.getName());
                }
            }
            final CustomValue dashPropertyValue = metadata.getCustomValue("dashloader:customproperty");
            if (dashPropertyValue != null) {
                try {
                    final CustomValue.CvArray values = dashPropertyValue.getAsArray();
                    for (CustomValue value : values) {
                        DashPropertyFactory factory = (DashPropertyFactory) Unsafe.allocateInstance(Class.forName(value.getAsString()));
                        addPropertyType(factory);
                        if (!metadata.getId().equals("dashloader"))
                            LOGGER.info("Added custom property: " + factory.getPropertyType().getSimpleName());
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("PropertyFactory: " + dashPropertyValue.getAsString() + " not found. MOD: " + metadata.getName());
                }
            }
        });
        LOGGER.info("[2/4] [" + Duration.between(start, Instant.now()).toMillis() + "ms] Initialized api.");
    }


    /**
     * <h1>E</h1>
     */
    private void initSerializers() {
        Instant start = Instant.now();
        serializers = new ConcurrentHashMap<>();
        ThreadHelper.exec(
                () -> addSerializer(DashMetadata.class, SerializerBuilder.create()),
                () -> addSerializer(DashModelData.class, SerializerBuilder.create()),
                () -> addSerializer(DashSpriteAtlasData.class, SerializerBuilder.create()),
                () -> addSerializer(DashBlockStateData.class, SerializerBuilder.create()),
                () -> addSerializer(DashParticleData.class, SerializerBuilder.create()),
                () -> addSerializer(DashFontManagerData.class, SerializerBuilder.create()),
                () -> addSerializer(DashSplashTextData.class, SerializerBuilder.create()),
                () -> addSerializer(RegistryBlockStateData.class, SerializerBuilder.create()),
                () -> addSerializer(RegistryFontData.class, SerializerBuilder.create().withSubclasses("fonts", DashBitmapFont.class, DashUnicodeFont.class, DashBlankFont.class)),
                () -> addSerializer(RegistryIdentifierData.class, SerializerBuilder.create()),
                () -> addSerializer(RegistryImageData.class, SerializerBuilder.create()),
                () -> addSerializer(RegistryModelData.class, SerializerBuilder.create().withSubclasses("models", modelMappings.values().stream().map(DashModelFactory::getDashModelType).sorted(Comparator.comparing(Class::getSimpleName)).toArray(Class[]::new))),
                () -> addSerializer(RegistryPredicateData.class, SerializerBuilder.create().withSubclasses("predicates", DashAndPredicate.class, DashSimplePredicate.class, DashOrPredicate.class, DashStaticPredicate.class)),
                () -> addSerializer(RegistryPropertyData.class, SerializerBuilder.create().withSubclasses("properties", propertyMappings.values().stream().map(DashPropertyFactory::getDashPropertyType).sorted(Comparator.comparing(Class::getSimpleName)).toArray(Class[]::new))),
                () -> addSerializer(RegistryPropertyValueData.class, SerializerBuilder.create().withSubclasses("values", propertyMappings.values().stream().map(DashPropertyFactory::getDashPropertyValueType).sorted(Comparator.comparing(Class::getSimpleName)).toArray(Class[]::new))),
                () -> addSerializer(RegistrySpriteData.class, SerializerBuilder.create()));
        LOGGER.info("[3/4] [" + Duration.between(start, Instant.now()).toMillis() + "ms] Created Serializers.");
    }

    private void addSerializer(Class<?> clazz, SerializerBuilder builder) {
        serializers.put(clazz, builder.withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE).build(clazz));
    }


}
