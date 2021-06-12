package net.quantumfusion.dashloader;

import com.mojang.blaze3d.platform.TextureUtil;
import io.activej.codegen.DefiningClassLoader;
import io.activej.serializer.BinarySerializer;
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
import net.quantumfusion.dashloader.data.DashMappingData;
import net.quantumfusion.dashloader.data.DashMetadata;
import net.quantumfusion.dashloader.data.DashRegistryData;
import net.quantumfusion.dashloader.data.mappings.*;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.mixin.accessor.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.util.DashCachePaths;
import net.quantumfusion.dashloader.util.DashCacheState;
import net.quantumfusion.dashloader.util.ThreadHelper;
import net.quantumfusion.dashloader.util.TimeHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.function.Function;

import static io.activej.codegen.ClassBuilder.CLASS_BUILDER_MARKER;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int totalTasks = 22;
    public static final short formatVersion = 2;
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
    private DashMetadata metadata;

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
        metadata = new DashMetadata(4, 20);
        state = DashCacheState.EMPTY;
        Instant start = Instant.now();
        initThreadPool();
        initSerializers();
        createDirectory();
        if (Arrays.stream(DashCachePaths.values()).allMatch(dashCachePaths -> dashCachePaths.getPath().toFile().exists())) {
            loadDashCache();
        }
        LOGGER.info("[4/4] Launching DashCache.");

        shutdownThreadPool();
        LOGGER.info("Loaded cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
    }

    public DashLoaderAPI getApi() {
        return api;
    }

    private void shutdownThreadPool() {
        THREADPOOL.shutdown();
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
        initSerializers();
        createDirectory();
        tasksComplete++;
        DashRegistry registry = new DashRegistry(this);
        MappingData mappings = new MappingData();

        logAndTask("Mapping Blockstates");
        mappings.setBlockStateData(new DashBlockStateData(stateLookup, registry));

        logAndTask("Mapping Models");
        mappings.setModelData(new DashModelData(models, registry));

        logAndTask("Mapping Particles");
        mappings.setParticleData(new DashParticleData(particleSprites, registry));

        logAndTask("Mapping Fonts");
        mappings.setFontManagerData(new DashFontManagerData(fonts, registry));

        logAndTask("Mapping Splash Text");
        mappings.setSplashTextData(new DashSplashTextData(splashText));

        logAndTask("Mapping Atlas");
        mappings.setSpriteAtlasData(new DashSpriteAtlasData(atlasManager, atlasData, registry, extraAtlases));

        serializeObject(registry.createData(), DashCachePaths.REGISTRY_CACHE.getPath(), "Cache");
        serializeObject(mappings.createData(), DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping");
        registry.apiReport(LOGGER);
        shutdownThreadPool();
        task = "Caching is now complete.";
        LOGGER.info("Created cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
    }

    private void initThreadPool() {
        THREADPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("dashloader-thread-" + worker.getPoolIndex());
            worker.setContextClassLoader(classLoader);
            return worker;
        }, null, true);
    }

    public void loadDashCache() {
        LOGGER.info("Starting DashLoader Deserialization");
        try {
            Instant time = Instant.now();
            DashRegistry registry = new DashRegistry(this);
            mappings = new MappingData();
            ThreadHelper.exec(
                    () -> registry.loadData(deserialize(DashRegistryData.class, DashCachePaths.REGISTRY_CACHE.getPath(), "Cache")),
                    () -> mappings.loadData(deserialize(DashMappingData.class, DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping"))
            );
            LOGGER.info(TimeHelper.getMs(time) + "ms");

            LOGGER.info("      Loading Registry");
            registry.toUndash();

            LOGGER.info("      Loading Mappings");
            atlasesToRegister.addAll(mappings.toUndash(registry));

            LOGGER.info("    Loaded DashLoader");
            stateLookupOut = mappings.stateLookupOut;
            state = DashCacheState.LOADED;
        } catch (Exception e) {
            state = DashCacheState.CRASHLOADER;
        }
    }

    public void applyDashCache(TextureManager textureManager, Profiler profiler) {
        //register textures
        profiler.push("atlas");
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
            T out = StreamInput.create(Files.newInputStream(path), 1048576).deserialize(serializer);
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


    private Path getSerializerPath(String name) {
        final File[] files = getModBoundDir().toFile().listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().endsWith(name + ".serializer")) {
                return Paths.get(file.toURI());
            }
        }
        return null;
    }

    private <K> void renameRawSerializer(String name, BinarySerializer<K> serializer) throws IOException {
        final String className = serializer.getClass().getName().replaceFirst("io.activej.codegen.", "");
        LOGGER.info("Created Serializer {}", className);
        final Path folder = getModBoundDir();
        final Path resolve = folder.resolve(className + ".class");
        resolve.toFile().renameTo(folder.resolve(className.replaceFirst("io.activej.serializer.", "") + "-" + name + ".serializer").toFile());
    }


    public Path getModBoundDir() {
        final Path resolve = DashLoader.getConfig().resolve("quantumfusion/dashloader/mods-" + metadata.modInfo + "/");
        if (!resolve.toFile().exists()) {
            resolve.toFile().mkdirs();
        }
        return resolve;
    }

    public Path getResourcePackBoundDir() {
        final Path resolve = getModBoundDir().resolve("resourcepacks-" + metadata.resourcePacks + "/");
        if (!resolve.toFile().exists()) {
            resolve.toFile().mkdirs();
        }
        return resolve;
    }

    private void initSerializers() {
        Instant start = Instant.now();
        final DefiningClassLoader definingClassLoader = DefiningClassLoader.create(classLoader);
        serializers.put(DashRegistryData.class, initSerializer(DashRegistryData.class, definingClassLoader, "dashregistry",
                (builder) -> {
                    api.initAPI();
                    return builder
                            .withSubclasses("fonts", api.fontTypes)
                            .withSubclasses("models", api.modelTypes)
                            .withSubclasses("predicates", api.predicateTypes)
                            .withSubclasses("properties", api.propertyTypes)
                            .withSubclasses("values", api.propertyValueTypes);
                }
        ));
        serializers.put(DashMappingData.class, initSerializer(DashMappingData.class, definingClassLoader, "dashmappings", (builder) -> builder));
        LOGGER.info("[{}ms] Initialized Serializers", TimeHelper.getMs(start));
    }

    private <K> BinarySerializer<K> initSerializer(Class<K> clazz, DefiningClassLoader classLoader, String id, Function<SerializerBuilder, SerializerBuilder> createSerializer) {
        final Path serializerPath = getSerializerPath(id);
        if (serializerPath != null) {
            try {
                final File serializerFile = serializerPath.toFile();
                if (serializerFile.exists()) return loadSerializerCache(classLoader, serializerFile);
            } catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final BinarySerializer<K> build = createSerializer.apply(SerializerBuilder.create()).withGeneratedBytecodePath(getModBoundDir()).build(clazz);
                renameRawSerializer(id, build);
                return build;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new DashException("Unable to create serializer");
    }

    private <K> BinarySerializer<K> loadSerializerCache(DefiningClassLoader classLoader, File serializer) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final byte[] bytes = IOUtils.toByteArray(FileUtils.openInputStream(serializer));
        final Object serializerObject = create(classLoader, "io.activej.codegen.io.activej.serializer." + serializer.getName().split("-")[0], bytes).getConstructor().newInstance();
        return (BinarySerializer<K>) serializerObject;
    }

    private <T> Class<T> create(DefiningClassLoader classLoader, String actualClassName, byte[] bytecode) {
        Class<T> aClass = (Class<T>) classLoader.defineClass(actualClassName, bytecode);
        try {
            Field field = aClass.getField(CLASS_BUILDER_MARKER);
            //noinspection ResultOfMethodCallIgnored
            field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new AssertionError(e);
        }
        return aClass;
    }


}
