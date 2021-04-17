package net.quantumfusion.dash;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.*;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.DashRegistry;
import net.quantumfusion.dash.cache.atlas.DashExtraAtlasData;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dash.cache.font.DashFontManagerData;
import net.quantumfusion.dash.cache.font.fonts.DashBitmapFont;
import net.quantumfusion.dash.cache.font.fonts.DashBlankFont;
import net.quantumfusion.dash.cache.font.fonts.DashUnicodeFont;
import net.quantumfusion.dash.cache.misc.DashLoaderInfo;
import net.quantumfusion.dash.cache.misc.DashParticleData;
import net.quantumfusion.dash.cache.models.*;
import net.quantumfusion.dash.misc.DashSplashTextData;
import net.quantumfusion.dash.util.TimeHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Dash implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static List<String> splashText;

    public static Path config = FabricLoader.getInstance().getConfigDir().normalize();
    public static boolean caching = true;

    public static BinarySerializer<DashSplashTextData> splashTextSerializer = SerializerBuilder.create().build(DashSplashTextData.class);


    public static final Object2ObjectMap<Class, BinarySerializer> serializers = new Object2ObjectOpenHashMap<>();
    public static final HashMap<Class<? extends BakedModel>, DashModel> modelMappings = new HashMap<>();

    public static DashCache loader = new DashCache();

    public static final Path registryPath = config.resolve("dash/registry.activej");
    public static final Path blockstatePath = config.resolve("dash/blockstate-mappings.activej");
    public static final Path modelPath = config.resolve("dash/model-mappings.activej");
    public static final Path atlasPath = config.resolve("dash/atlas-mappings.activej");
    public static final Path particlePath = config.resolve("dash/particle-mappings.activej");
    public static final Path fontPath = config.resolve("dash/font-mappings.activej");
    public static final Path extraAtlasPath = config.resolve("dash/extraatlas-mappings.activej");

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
        DashLoaderInfo.create();
        LOGGER.info("Starting dash thread.");
        Thread dash = new Thread(() -> {
            Instant start = Instant.now();
            initModelMappings();
            initSerializers();
            makeFolders();
            LOGGER.info("Starting Dash init");
            loader.init();
            misc();
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

    private static void misc() {
        Arrays.stream(listCacheFiles("dash")).parallel().forEach(file -> {
            if (file.getName().equals("splash.activej")) {
                try {
                    splashText = StreamInput.create(new FileInputStream(prepareAccess(file))).deserialize(splashTextSerializer).splashList;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void initModelMappings() {
        modelMappings.put(BasicBakedModel.class, new DashBasicBakedModel());
        modelMappings.put(BuiltinBakedModel.class, new DashBuiltinBakedModel());
        modelMappings.put(MultipartBakedModel.class, new DashMultipartBakedModel());
        modelMappings.put(WeightedBakedModel.class, new DashWeightedBakedModel());
    }

    private static void initSerializers() {
        ArrayList<Class<?>> list = new ArrayList<>();
        modelMappings.values().forEach(dashModel -> list.add(dashModel.getClass()));

        serializers.put(DashRegistry.class,
                SerializerBuilder.create()
                        .withSubclasses("models", DashBasicBakedModel.class,DashBuiltinBakedModel.class,DashMultipartBakedModel.class,DashWeightedBakedModel.class)
                        .withSubclasses("fonts", DashBitmapFont.class, DashUnicodeFont.class, DashBlankFont.class)
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashRegistry.class));


        serializers.put(DashModelData.class,
                SerializerBuilder.create()
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashModelData.class));

        serializers.put(DashSpriteAtlasManager.class,
                SerializerBuilder.create()
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashSpriteAtlasManager.class));

        serializers.put(DashBlockStateData.class,
                SerializerBuilder.create()
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashBlockStateData.class));


        serializers.put(DashParticleData.class,
                SerializerBuilder.create()
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashParticleData.class));

        serializers.put(DashExtraAtlasData.class,
                SerializerBuilder.create()
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashExtraAtlasData.class));

        serializers.put(DashFontManagerData.class,
                SerializerBuilder.create()
                        .withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE)
                        .build(DashFontManagerData.class));
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
