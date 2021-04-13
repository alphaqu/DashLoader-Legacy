package net.quantumfusion.dash;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.cache.DashRegistry;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dash.cache.models.*;
import net.quantumfusion.dash.misc.DashParticleTextureData;
import net.quantumfusion.dash.misc.DashSplashTextData;
import net.quantumfusion.dash.util.TimeHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.quantumfusion.dash.util.ThreadHelper.awaitTerminationAfterShutdown;

public class Dash implements ModInitializer {

    public static Logger LOGGER;
    public static HashMap<String, NativeImage> fontCache;
    public static HashMap<Identifier, List<Identifier>> particleCache;
    public static List<String> splashText;

    public static Path config = FabricLoader.getInstance().getConfigDir().normalize();
    public static boolean caching = true;

    public static BinarySerializer<DashSplashTextData> splashTextSerializer = SerializerBuilder.create().build(DashSplashTextData.class);
    public static BinarySerializer<DashParticleTextureData> dashParticleTextureDataSerializer = SerializerBuilder.create().build(DashParticleTextureData.class);


    public static BinarySerializer<DashModelData> modelSerializer = SerializerBuilder.create()
            .withSubclasses("models", DashBasicBakedModel.class, DashBuiltinBakedModel.class, DashMultipartBakedModel.class, DashWeightedBakedModel.class)
            .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
            .build(DashModelData.class);

    public static BinarySerializer<DashBlockStateData> blockStateSerializer = SerializerBuilder.create()
            .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
            .build(DashBlockStateData.class);

    public static BinarySerializer<DashSpriteAtlasManager> atlasSerializer = SerializerBuilder.create()
            .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
            .build(DashSpriteAtlasManager.class);

    public static BinarySerializer<DashRegistry> registrySerializer = SerializerBuilder.create()
            .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
            .build(DashRegistry.class);

    public static DashModelLoader loader = new DashModelLoader();

    public static   Path registryPath = config.resolve("dash/registry.activej");
    public static   Path modelPath = config.resolve("dash/model-mappings.activej");
    public static  Path atlasPath = config.resolve("dash/atlas-mappings.activej");

    public static Unsafe getUnsafe() {
        Field f = null;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assert f != null;
        f.setAccessible(true);
        try {
            return (Unsafe) f.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void reload() {
        caching = true;
        LOGGER = LogManager.getLogger();
        particleCache = new HashMap<>();
        fontCache = new HashMap<>();
        LOGGER.info("Starting dash thread.");
        Thread dash = new Thread(() -> {
            Instant start = Instant.now();
            makeFolders();
            int threads = Runtime.getRuntime().availableProcessors();
            LOGGER.info("Starting dash threadpool");
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            loader.init(executorService);
            misc(executorService);
            fontCacheRegister(executorService);
            particleCacheRegister(executorService);
            awaitTerminationAfterShutdown(executorService);
            Instant stop = Instant.now();
            LOGGER.info("Loaded cache in " + TimeHelper.getDecimalMs(start, stop) + "ms");
            caching = false;
        });
        dash.setName("dash-manager");
        dash.start();
    }

    private static void makeFolders() {
        createDirectory("dash");
        createDirectory("dash/fonts");
        createDirectory("dash/particles");
    }

    private static void misc(ExecutorService executorService) {
        Arrays.stream(listCacheFiles("dash")).forEach(file -> executorService.execute(() -> {
            if (file.getName().equals("splash.activej")) {
                try {
                    splashText = StreamInput.create(new FileInputStream(prepareAccess(file))).deserialize(splashTextSerializer).splashList;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private static void particleCacheRegister(ExecutorService executorService) {
        Arrays.stream(listCacheFiles("dash/particles")).forEach(particle -> executorService.execute(() -> {
            try {
                DashParticleTextureData cache = StreamInput.create(new FileInputStream(prepareAccess(particle))).deserialize(dashParticleTextureDataSerializer);
                particleCache.put(cache.id.toUndash(), cache.toUndash().getTextureList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private static void fontCacheRegister(ExecutorService executorService) {
        Arrays.stream(listCacheFiles("dash/fonts")).forEach(fontFile -> executorService.execute(() -> {
            try {
                fontCache.put(prepareAccess(fontFile).getName().split("-")[1].replace(".png", ""), NativeImage.read(NativeImage.Format.ABGR, new BufferedInputStream(new FileInputStream(fontFile))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private static File prepareAccess(File file) {
        if (!file.canWrite()) {
            file.setWritable(true);
        }
        if (!file.canRead()) {
            file.setReadable(true);
        }
        return file;
    }

    private static File[] listCacheFiles(String s) {
        return new File(String.valueOf(config.resolve(s))).listFiles();
    }

    private static void createDirectory(String s) {
        prepareAccess(new File(String.valueOf(config.resolve(s)))).mkdir();
    }

    @Override
    public void onInitialize() {
    }
}
