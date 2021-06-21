package net.quantumfusion.dashloader;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.api.DashLoaderAPI;
import net.quantumfusion.dashloader.data.DashMetadata;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureClassLoader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import static net.quantumfusion.dashloader.util.DashSerializers.MAPPING_SERIALIZER;
import static net.quantumfusion.dashloader.util.DashSerializers.REGISTRY_SERIALIZER;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String VERSION = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    public static final TaskHandler TASK_HANDLER = new TaskHandler(LOGGER);
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().normalize();
    private static final VanillaData VANILLA_DATA = new VanillaData();
    public static ForkJoinPool THREAD_POOL;
    private static boolean shouldReload = true;
    private static DashLoader instance;
    private final ClassLoaderWrapper classLoader;
    private final DashLoaderAPI api;
    public DashCacheState state;
    private DashMappings mappings;
    private DashMetadata metadata;

    public DashLoader(ClassLoader classLoader) {
        instance = this;
        this.classLoader = new ClassLoaderWrapper((SecureClassLoader) classLoader);
        this.api = new DashLoaderAPI();
        LOGGER.info("Created DashLoader");
    }

    public static Path getConfig() {
        return CONFIG;
    }

    public static DashLoader getInstance() {
        return instance;
    }

    public static VanillaData getVanillaData() {
        return VANILLA_DATA;
    }

    public DashMappings getMappings() {
        return mappings;
    }

    public DashLoaderAPI getApi() {
        return api;
    }

    public void requestReload() {
        shouldReload = true;
    }

    public ClassLoaderWrapper getAssignedClassLoader() {
        return classLoader;
    }

    public void initialize() {
        Instant start = Instant.now();
        LOGGER.info("Initializing DashLoader " + VERSION + ".");
        final FabricLoader instance = FabricLoader.getInstance();
        if (instance.isDevelopmentEnvironment()) {
            LOGGER.warn("DashLoader launched in dev.");
        }
        metadata = new DashMetadata();
        metadata.setMods(instance);
        state = DashCacheState.EMPTY;
        initThreadPool();
        DashSerializers.initSerializers();
        DashReport.addEntry(new DashReport.Entry(start, "Initialization", true));
        LOGGER.info("Initialized DashLoader");
    }

    public void reload(Collection<String> resourcePacks) {
        if (shouldReload) {
            final Instant time = Instant.now();
            DashReport.addTime(time, "From reload");
            state = DashCacheState.EMPTY;
            if (THREAD_POOL.isTerminated()) {
                initThreadPool();
            }
            metadata.setResourcePacks(resourcePacks);
            LOGGER.info("Reloading DashLoader. [mod-hash: {}] [resource-hash: {}]", metadata.modInfo, metadata.resourcePacks);
            if (Arrays.stream(DashCachePaths.values()).allMatch(dashCachePaths -> Files.exists(dashCachePaths.getPath()))) {
                loadDashCache();
            }
            shutdownThreadPool();
            LOGGER.info("Reloaded DashLoader");
            shouldReload = false;
            DashReport.addEntry(new DashReport.Entry(time, "Reload", true));
        }
    }

    public void loadDashCache() {
        LOGGER.info("Starting DashLoader Deserialization");
        try {
            DashRegistry registry = new DashRegistry(this);
            ThreadHelper.exec(
                    () -> registry.loadData(REGISTRY_SERIALIZER.deserializeObject(DashCachePaths.REGISTRY_CACHE.getPath(), "Cache")),
                    () -> mappings = (MAPPING_SERIALIZER.deserializeObject(DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping"))
            );
            assert mappings != null;


            LOGGER.info("      Loading Registry");
            registry.toUndash();

            LOGGER.info("      Loading Mappings");
            mappings.toUndash(registry, VANILLA_DATA);


            LOGGER.info("    Loaded DashLoader");
            state = DashCacheState.LOADED;
        } catch (Exception e) {
            state = DashCacheState.CRASHLOADER;
            LOGGER.error("DashLoader has devolved to CrashLoader???", e);
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

    public void saveDashCache() {
        Instant start = Instant.now();
        TASK_HANDLER.reset();
        initThreadPool();
        api.initAPI();
        TASK_HANDLER.completedTask();
        DashRegistry registry = new DashRegistry(this);
        DashMappings mappings = new DashMappings();
        mappings.loadVanillaData(VANILLA_DATA, registry, TASK_HANDLER);
        REGISTRY_SERIALIZER.serializeObject(registry.createData(), DashCachePaths.REGISTRY_CACHE.getPath(), "Cache");
        MAPPING_SERIALIZER.serializeObject(mappings, DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping");
        registry.apiReport(LOGGER);
        shutdownThreadPool();
        TASK_HANDLER.setCurrentTask("Caching is now complete.");
        LOGGER.info("Created cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
    }

    private void initThreadPool() {
        THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("dashloader-thread-" + worker.getPoolIndex());
            worker.setContextClassLoader(classLoader);
            return worker;
        }, null, true);
    }

    private void shutdownThreadPool() {
        THREAD_POOL.shutdown();
    }


    public Path getModBoundDir() {
        try {
            final Path resolve = DashLoader.getConfig().resolve("quantumfusion/dashloader/mods-" + metadata.modInfo + "/");
            return Files.createDirectories(resolve);
        } catch (IOException e) {
            LOGGER.error("Could not create ModBoundDir: ", e);
        }
        throw new IllegalStateException();
    }

    public Path getResourcePackBoundDir() {
        try {
            final Path resolve = getModBoundDir().resolve("resourcepacks-" + metadata.resourcePacks + "/");
            return Files.createDirectories(resolve);
        } catch (IOException e) {
            LOGGER.error("Could not create ResourcePackBoundDir: ", e);
        }
        throw new IllegalStateException();
    }


}
