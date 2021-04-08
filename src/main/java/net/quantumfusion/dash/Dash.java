package net.quantumfusion.dash;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.misc.DashParticleTextureData;
import net.quantumfusion.dash.misc.DashSplashTextData;
import net.quantumfusion.dash.model.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.model.object.DashJsonUnbakedModel;
import net.quantumfusion.dash.sprite.SpriteInfoCache;
import net.quantumfusion.dash.util.TimeHelper;
import sun.misc.Unsafe;

import java.io.BufferedInputStream;
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

    public static Object2ObjectMap<String,Material> materialRegistry = new Object2ObjectOpenHashMap<>();

    @Override
    public void onInitialize() {
    }

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


    public static SpriteAtlasManager compare(SpriteAtlasManager atlasManagerCache) {
        SpriteAtlasManager out = new DashSpriteAtlasManager(atlasManagerCache).toUndash();
        System.out.println("test");
        return out;
    }

    public static void reload() {
        registerMaterials();
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

    private static void registerMaterials() {
      materialRegistry.put("AIR",Material.AIR);
      materialRegistry.put("STRUCTURE_VOID",Material.STRUCTURE_VOID);
      materialRegistry.put("PORTAL",Material.PORTAL);
      materialRegistry.put("CARPET",Material.CARPET);
      materialRegistry.put("PLANT",Material.PLANT);
      materialRegistry.put("UNDERWATER_PLANT",Material.UNDERWATER_PLANT);
      materialRegistry.put("REPLACEABLE_PLANT",Material.REPLACEABLE_PLANT);
      materialRegistry.put("NETHER_SHOOTS",Material.NETHER_SHOOTS);
      materialRegistry.put("REPLACEABLE_UNDERWATER_PLANT",Material.REPLACEABLE_UNDERWATER_PLANT);
      materialRegistry.put("WATER",Material.WATER);
      materialRegistry.put("BUBBLE_COLUMN",Material.BUBBLE_COLUMN);
      materialRegistry.put("LAVA",Material.LAVA);
      materialRegistry.put("SNOW_LAYER",Material.SNOW_LAYER);
      materialRegistry.put("FIRE",Material.FIRE);
      materialRegistry.put("SUPPORTED",Material.SUPPORTED);
      materialRegistry.put("COBWEB",Material.COBWEB);
      materialRegistry.put("REDSTONE_LAMP",Material.REDSTONE_LAMP);
      materialRegistry.put("ORGANIC_PRODUCT",Material.ORGANIC_PRODUCT);
      materialRegistry.put("SOIL",Material.SOIL);
      materialRegistry.put("SOLID_ORGANIC",Material.SOLID_ORGANIC);
      materialRegistry.put("DENSE_ICE",Material.DENSE_ICE);
      materialRegistry.put("AGGREGATE",Material.AGGREGATE);
      materialRegistry.put("SPONGE",Material.SPONGE);
      materialRegistry.put("SHULKER_BOX",Material.SHULKER_BOX);
      materialRegistry.put("WOOD",Material.WOOD);
      materialRegistry.put("NETHER_WOOD",Material.NETHER_WOOD);
      materialRegistry.put("BAMBOO_SAPLING",Material.BAMBOO_SAPLING);
      materialRegistry.put("BAMBOO",Material.BAMBOO);
      materialRegistry.put("WOOL",Material.WOOL);
      materialRegistry.put("TNT",Material.TNT);
      materialRegistry.put("LEAVES",Material.LEAVES);
      materialRegistry.put("GLASS",Material.GLASS);
      materialRegistry.put("ICE",Material.ICE);
      materialRegistry.put("CACTUS",Material.CACTUS);
      materialRegistry.put("STONE",Material.STONE);
      materialRegistry.put("METAL",Material.METAL);
      materialRegistry.put("SNOW_BLOCK",Material.SNOW_BLOCK);
      materialRegistry.put("REPAIR_STATION",Material.REPAIR_STATION);
      materialRegistry.put("BARRIER",Material.BARRIER);
      materialRegistry.put("PISTON",Material.PISTON);
      materialRegistry.put("UNUSED_PLANT",Material.UNUSED_PLANT);
      materialRegistry.put("GOURD",Material.GOURD);
      materialRegistry.put("EGG",Material.EGG);
      materialRegistry.put("CAKE",Material.CAKE);

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
