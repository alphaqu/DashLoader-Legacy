package net.quantumfusion.dashloader;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.quantumfusion.dashloader.api.DashLoaderAPI;
import net.quantumfusion.dashloader.api.feature.FeatureHandler;
import net.quantumfusion.dashloader.client.DashCacheOverlay;
import net.quantumfusion.dashloader.data.DashRegistryData;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.data.registry.RegistryImageData;
import net.quantumfusion.dashloader.data.registry.RegistryModelData;
import net.quantumfusion.dashloader.data.serializers.DashSerializers;
import net.quantumfusion.dashloader.mixin.accessor.MinecraftClientAccessor;
import net.quantumfusion.dashloader.util.ClassLoaderWrapper;
import net.quantumfusion.dashloader.util.DashReport;
import net.quantumfusion.dashloader.util.ThreadHelper;
import net.quantumfusion.dashloader.util.TimeHelper;
import net.quantumfusion.dashloader.util.enums.DashCachePaths;
import net.quantumfusion.dashloader.util.enums.DashCacheState;
import org.apache.commons.lang3.tuple.Triple;
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

import static net.quantumfusion.dashloader.data.serializers.DashSerializers.*;

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
            final MinecraftClient client = MinecraftClient.getInstance();
            client.setOverlay(new DashCacheOverlay(client));
            ((MinecraftClientAccessor) client).callRender(false);
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
                    () -> registry.loadModelData(MODEL_SERIALIZER.deserializeObject(DashCachePaths.REGISTRY_MODEL_CACHE.getPath(), "Model Cache")),
                    () -> registry.loadImageData(IMAGE_SERIALIZER.deserializeObject(DashCachePaths.REGISTRY_IMAGE_CACHE.getPath(), "Image Cache")),
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
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
                try {
                    Files.deleteIfExists(getModBoundDir());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public void finish() {
        VANILLA_DATA.clearData();

    }


    public void saveDashCache() {
        Instant start = Instant.now();
        TASK_HANDLER.reset();
        TaskHandler.setTotalTasks(FeatureHandler.calculateTasks());
        initThreadPool();
        api.initAPI();
        TASK_HANDLER.completedTask();
        DashRegistry registry = new DashRegistry(this);
        DashMappings mappings = new DashMappings();
        mappings.loadVanillaData(VANILLA_DATA, registry, TASK_HANDLER);
        final Triple<DashRegistryData, RegistryImageData, RegistryModelData> data = registry.createData();
        REGISTRY_SERIALIZER.serializeObject(data.getLeft(), DashCachePaths.REGISTRY_CACHE.getPath(), "Cache");
        IMAGE_SERIALIZER.serializeObject(data.getMiddle(), DashCachePaths.REGISTRY_IMAGE_CACHE.getPath(), "Image Cache");
        MODEL_SERIALIZER.serializeObject(data.getRight(), DashCachePaths.REGISTRY_MODEL_CACHE.getPath(), "Model Cache");
        MAPPING_SERIALIZER.serializeObject(mappings, DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping");
        registry.apiReport(LOGGER);
        shutdownThreadPool();
        TASK_HANDLER.setCurrentTask("Caching is now complete.");
        LOGGER.info("Created cache in " + TimeHelper.getDecimalS(start, Instant.now()) + "s");
    }

    private void initThreadPool() {
        final ForkJoinPool.ForkJoinWorkerThreadFactory factory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, exception) -> LOGGER.fatal("Thread {} failed. Reason: ", thread.getName(), exception);
        THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), pool -> {
            final ForkJoinWorkerThread worker = factory.newThread(pool);
            worker.setName("dashloader-thread-" + worker.getPoolIndex());
            worker.setContextClassLoader(classLoader);
            return worker;
        }, uncaughtExceptionHandler, true);

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


    public static class TaskHandler {
        public static int TOTALTASKS = 9;
        private static float taskStep = 1f / TOTALTASKS;
        private final Logger logger;
        private String task;
        private int tasksComplete;

        private int subTotalTasks = 1;
        private int subTasksComplete = 0;

        public TaskHandler(Logger logger) {
            task = "Starting DashLoader";
            tasksComplete = 0;
            this.logger = logger;
        }

        public static void setTotalTasks(int tasks) {
            TOTALTASKS = tasks;
            taskStep = 1f / TOTALTASKS;
        }

        public void logAndTask(String s) {
            logger.info(s);
            tasksComplete++;
            task = s;
        }

        public void reset() {
            tasksComplete = 0;
            subTotalTasks = 1;
            subTasksComplete = 0;
        }

        public void completedTask() {
            tasksComplete++;
        }

        public void setCurrentTask(String task) {
            this.task = task;
        }

        public void setSubtasks(int tasks) {
            subTotalTasks = tasks;
            subTasksComplete = 0;
        }

        public void completedSubTask() {
            subTasksComplete++;
        }

        public Text getText() {
            return Text.of("(" + tasksComplete + "/" + TOTALTASKS + ") " + task);
        }

        public Text getSubText() {
            return TOTALTASKS == tasksComplete ? Text.of("") : Text.of("[" + subTasksComplete + "/" + subTotalTasks + "] ");
        }

        public double getProgress() {
            return (subTasksComplete == subTotalTasks && tasksComplete == TOTALTASKS) ? 1 : (tasksComplete == 0 ? 0 : tasksComplete / (float) TOTALTASKS) + (((float) subTasksComplete / subTotalTasks) * taskStep);
        }
    }

    public static class DashMetadata {
        public String modInfo;
        public String resourcePacks;


        public DashMetadata() {

        }

        public void setMods(FabricLoader loader) {
            long modInfoData = 0;
            for (ModContainer mod : loader.getAllMods()) {
                for (char c : mod.getMetadata().getVersion().getFriendlyString().toCharArray()) {
                    modInfoData += c;
                }
            }
            modInfo = Long.toHexString(modInfoData);
        }

        public void setResourcePacks(Collection<String> resourcePacks) {
            long resourcePackData = 0;
            for (String resourcePack : resourcePacks) {
                for (char c : resourcePack.toCharArray()) {
                    resourcePackData += c;
                }
            }
            this.resourcePacks = Long.toHexString(resourcePackData);
        }


        public String getId() {
            return modInfo + "-" + resourcePacks;
        }

    }
}
