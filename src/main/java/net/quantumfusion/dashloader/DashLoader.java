package net.quantumfusion.dashloader;

import com.mojang.blaze3d.platform.TextureUtil;
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
import net.quantumfusion.dashloader.data.DashMetadata;
import net.quantumfusion.dashloader.data.mappings.*;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.mixin.accessor.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import static net.quantumfusion.dashloader.util.DashSerializers.MAPPING_SERIALIZER;
import static net.quantumfusion.dashloader.util.DashSerializers.REGISTRY_SERIALIZER;

public class DashLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int totalTasks = 22;
    public static final short formatVersion = 2;
    public static final String version = FabricLoader.getInstance().getModContainer("dashloader").get().getMetadata().getVersion().getFriendlyString();
    private static final Path config = FabricLoader.getInstance().getConfigDir().normalize();
    public static ForkJoinPool THREADPOOL;
    public static String task = "Starting DashLoader";
    private static DashLoader instance;
    public final Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    public final Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();
    public final List<SpriteAtlasTexture> atlasesToRegister;
    private final ClassLoader classLoader;
    private final List<SpriteAtlasTexture> extraAtlases;
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

    public void initialize() {
        LOGGER.info("Starting DashLoader thread.");
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.warn("DashLoader launched in dev.");
        }
        metadata = new DashMetadata(4, 20);
        state = DashCacheState.EMPTY;
        Instant start = Instant.now();
        initThreadPool();
        createDirectory();
        DashSerializers.initSerializers();
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

    public void setBakedModelAssets(SpriteAtlasManager atlasManager,
                                    Object2IntMap<BlockState> stateLookup,
                                    Map<Identifier, BakedModel> models) {
        this.atlasManager = atlasManager;
        this.models = models;
        this.stateLookup = stateLookup;
    }

    public void setParticleManagerAssets(Map<Identifier, ParticleManager.SimpleSpriteProvider> particles, SpriteAtlasTexture atlas) {
        this.particleSprites = particles;
        addExtraAtlasAssets(atlas);
    }

    public void setSplashTextAssets(List<String> splashText) {
        this.splashText = splashText;
    }

    public void saveDashCache() {
        Instant start = Instant.now();
        initThreadPool();
        api.initAPI();
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

        REGISTRY_SERIALIZER.serializeObject(registry.createData(), DashCachePaths.REGISTRY_CACHE.getPath(), "Cache");
        MAPPING_SERIALIZER.serializeObject(mappings.createData(), DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping");
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
                    () -> registry.loadData(REGISTRY_SERIALIZER.deserializeObject(DashCachePaths.REGISTRY_CACHE.getPath(), "Cache")),
                    () -> mappings.loadData(MAPPING_SERIALIZER.deserializeObject(DashCachePaths.MAPPINGS_CACHE.getPath(), "Mapping"))
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
