package net.quantumfusion.dashloader;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.api.DashLoaderAPI;
import net.quantumfusion.dashloader.data.DashMetadata;
import net.quantumfusion.dashloader.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import static net.quantumfusion.dashloader.util.DashSerializers.MAPPING_SERIALIZER;
import static net.quantumfusion.dashloader.util.DashSerializers.REGISTRY_SERIALIZER;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String version = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().normalize();
    public static final TaskHandler TASK_HANDLER = new TaskHandler(LOGGER);
    public static ForkJoinPool THREADPOOL;
    private static DashLoader instance;
    private final VanillaData vanillaData = new VanillaData();
    private final ClassLoader classLoader;
    private final DashLoaderAPI api;
    @Nullable
    private DashMappings mappings;
    private DashMetadata metadata;
    public DashCacheState state;

    public DashLoader(ClassLoader classLoader) {
        LOGGER.info("Creating DashLoader Instance");
        instance = this;
        this.classLoader = classLoader;
        api = new DashLoaderAPI();
        LOGGER.info("Created DashLoader with classloader: " + classLoader.getClass().getSimpleName());
    }

    public static Path getConfig() {
        return CONFIG;
    }

    public static DashLoader getInstance() {
        return instance;
    }

    public static VanillaData getVanillaData() {
        return DashLoader.getInstance().vanillaData;
    }

    public DashMappings getMappings() {
        return mappings;
    }

    public void initialize() {
        LOGGER.info("Initializing DashLoader.");
        final FabricLoader instance = FabricLoader.getInstance();
        if (instance.isDevelopmentEnvironment()) {
            LOGGER.warn("DashLoader launched in dev.");
        }
        metadata = new DashMetadata();
        metadata.setMods(instance);
        state = DashCacheState.EMPTY;
        initThreadPool();
        createDirectory();
        DashSerializers.initSerializers();
        LOGGER.info("Initialized DashLoader");


    }

    public void reload(Collection<String> resourcePacks) {
        LOGGER.info("Reloading DashLoader");
        metadata.setResourcePacks(resourcePacks);
        if (Arrays.stream(DashCachePaths.values()).allMatch(dashCachePaths -> dashCachePaths.getPath().toFile().exists())) {
            loadDashCache();
        }
        shutdownThreadPool();
        LOGGER.info("Reloaded DashLoader");
    }

    public DashLoaderAPI getApi() {
        return api;
    }

    private void shutdownThreadPool() {
        THREADPOOL.shutdown();
    }

    private void createDirectory() {
        prepareAccess(new File(String.valueOf(CONFIG.resolve("quantumfusion/dashloader")))).mkdirs();
    }

    public void saveDashCache() {
        Instant start = Instant.now();
        initThreadPool();
        api.initAPI();
        TASK_HANDLER.completedTask();
        DashRegistry registry = new DashRegistry(this);
        DashMappings mappings = new DashMappings();
        mappings.loadVanillaData(vanillaData, registry, TASK_HANDLER);
        REGISTRY_SERIALIZER.serializeObject(registry.createData(), DashCachePaths.REGISTRY_CACHE.getPath(), "Cache");
        MAPPING_SERIALIZER.serializeObject(mappings.createData(), DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping");
        registry.apiReport(LOGGER);
        shutdownThreadPool();
        TASK_HANDLER.setCurrentTask("Caching is now complete.");
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
            DashMappings mappings = new DashMappings();
            ThreadHelper.exec(
                    () -> registry.loadData(REGISTRY_SERIALIZER.deserializeObject(DashCachePaths.REGISTRY_CACHE.getPath(), "Cache")),
                    () -> mappings.loadCacheData(MAPPING_SERIALIZER.deserializeObject(DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping"))
            );
            LOGGER.info(TimeHelper.getMs(time) + "ms");

            LOGGER.info("      Loading Registry");
            registry.toUndash();

            LOGGER.info("      Loading Mappings");
            mappings.toUndash(registry, vanillaData);
            this.mappings = mappings;

            LOGGER.info("    Loaded DashLoader");
            state = DashCacheState.LOADED;
        } catch (Exception e) {
            state = DashCacheState.CRASHLOADER;
        }
    }

    public void applyDashCache(TextureManager textureManager, Profiler profiler) {
        //register textures
        profiler.push("atlas");
        if (mappings != null) {
            mappings.registerAtlases(textureManager);
        }
        profiler.swap("baking");
        profiler.pop();
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

    public ClassLoader getAssignedClassLoader() {
        return classLoader;
    }


}
