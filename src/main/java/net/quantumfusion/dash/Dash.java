package net.quantumfusion.dash;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.misc.DashParticleTextureData;
import net.quantumfusion.dash.misc.DashSplashTextData;
import net.quantumfusion.dash.model.object.DashJsonUnbakedModel;
import net.quantumfusion.dash.sprite.SpriteInfoCache;
import net.quantumfusion.dash.util.TimeHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.quantumfusion.dash.util.ThreadHelper.awaitTerminationAfterShutdown;

public class Dash implements ModInitializer {
    public static boolean modelCache = false;

    public static HashMap<String, NativeImage> fontCache;
    public static HashMap<String, NativeImage> spriteCache;
    public static HashMap<Identifier, JsonUnbakedModel> jsonModelMap;
    public static HashMap<Identifier, List<Identifier>> particleCache;
    public static HashMap<Identifier, ArrayList<Sprite.Info>> spriteInfoCache;
    public static List<String> splashText;

    public static Path config = FabricLoader.getInstance().getConfigDir().normalize();
    public static boolean caching = true;
    public static ExecutorService modelLoading;

    public static BinarySerializer<SpriteInfoCache> spriteSerializer = SerializerBuilder.create().build(SpriteInfoCache.class);
    public static BinarySerializer<DashJsonUnbakedModel> jsonModelSerializer = SerializerBuilder.create().build(DashJsonUnbakedModel.class);
    public static BinarySerializer<DashSplashTextData> splashTextSerializer = SerializerBuilder.create().build(DashSplashTextData.class);
    public static BinarySerializer<DashParticleTextureData> dashParticleTextureDataSerializer = SerializerBuilder.create().build(DashParticleTextureData.class);

    @Override
    public void onInitialize() {
    }

    public static void reload() {
        caching = true;
        particleCache = new HashMap<>();
        spriteInfoCache = new HashMap<>();
        jsonModelMap = new HashMap<>();
        fontCache = new HashMap<>();
        spriteCache = new HashMap<>();
        modelLoading = Executors.newFixedThreadPool(4);
        System.out.println("Starting dash thread.");
        Instant start = Instant.now();
        Thread dash = new Thread(() -> {
            makeFolders();
            int threads = Runtime.getRuntime().availableProcessors();
            System.out.println("Starting dash with " + threads + " + 1 threads.");
            ExecutorService executorService = Executors.newFixedThreadPool(Math.max(threads - 1,1));
            misc(executorService);
            fontCacheRegister(executorService);
            spriteInfoRegister(executorService);
            spriteRegister(executorService);
            jsonModelRegister(executorService);
            particleCacheRegister(executorService);
            awaitTerminationAfterShutdown(executorService);
            System.out.println("Loaded cache in " + TimeHelper.getDecimalMs(start, Instant.now()) + "ms");
            caching = false;
        });
        dash.setName("dash-manager");
        dash.start();
    }

    private static void makeFolders() {
        createDirectory("dash");
        createDirectory("dash/fonts");
        createDirectory("dash/models");
        createDirectory("dash/particles");
        createDirectory("dash/sprite");
        createDirectory("dash/sprite/info");
    }


    private static void misc(ExecutorService executorService) {
        Arrays.stream(listCacheFiles("dash")).forEach(file -> executorService.execute(() -> {
            if(file.getName().equals("splash.activej")){
                try {
                    System.out.println("loaded splash");
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

    private static void spriteInfoRegister(ExecutorService executorService) {
        Arrays.stream(listCacheFiles("dash/sprite/info")).forEach(spriteInfoFile -> executorService.execute(() -> {
            try {
                SpriteInfoCache cache = StreamInput.create(new FileInputStream(prepareAccess(spriteInfoFile))).deserialize(spriteSerializer);
                spriteInfoCache.put(cache.id.toUndash(), cache.toUndash());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private static void jsonModelRegister(ExecutorService executorService) {
        if (modelCache) {
            Arrays.stream(listCacheFiles("dash/models")).forEach(jsonModelFile -> executorService.execute(() -> {
                try {
                    DashJsonUnbakedModel cache = StreamInput.create(new FileInputStream(prepareAccess(jsonModelFile))).deserialize(jsonModelSerializer);
                    jsonModelMap.put(cache.id.toUndash(), cache.toUndash());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }
    }


    private static void fontCacheRegister(ExecutorService executorService) {
        System.out.println(config.resolve("dash"));
        Arrays.stream(listCacheFiles("dash/fonts")).forEach(fontFile -> executorService.execute(() -> {
            try {
                fontCache.put(prepareAccess(fontFile).getName().split("-")[1].replace(".png", ""), NativeImage.read(NativeImage.Format.ABGR, new BufferedInputStream(new FileInputStream(fontFile))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private static void spriteRegister(ExecutorService executorService) {

        Arrays.stream(listCacheFiles("dash/sprite")).forEach(spriteFile -> executorService.execute(() -> {
            try {
                if (spriteFile.isDirectory()) {
                    return;
                }
                prepareAccess(spriteFile);
                spriteCache.put(spriteFile.getName().replace(".png", ""), NativeImage.read(new FileInputStream(spriteFile)));
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
}
