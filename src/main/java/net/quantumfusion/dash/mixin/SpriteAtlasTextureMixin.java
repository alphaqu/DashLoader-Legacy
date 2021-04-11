package net.quantumfusion.dash.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.texture.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.sprite.FastSpriteAtlas;
import net.quantumfusion.dash.util.StringHelper;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private int maxTextureSize;
    @Shadow
    @Final
    private Identifier id;

    @Shadow
    protected abstract Identifier getTexturePath(Identifier identifier);

    @Inject(method = "stitch(Lnet/minecraft/resource/ResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/util/profiler/Profiler;I)Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void stichOverride(ResourceManager resourceManager, Stream<Identifier> idStream, Profiler profiler, int mipmapLevel, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir) {
        profiler.push("preparing");
        Set<Identifier> set = idStream.peek((identifier) -> {
            if (identifier == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        int i = this.maxTextureSize;
        TextureStitcher textureStitcher = new TextureStitcher(i, i, mipmapLevel);
        int j = Integer.MAX_VALUE;
        int k = 1 << mipmapLevel;
        profiler.swap("extracting_frames");

        Sprite.Info info;
        int p;
        for (Iterator<Sprite.Info> var10 = FastSpriteAtlas.loadSprites(resourceManager, set, id).iterator(); var10.hasNext(); textureStitcher.add(info)) {
            info = var10.next();
            j = Math.min(j, Math.min(info.getWidth(), info.getHeight()));
            p = Math.min(Integer.lowestOneBit(info.getWidth()), Integer.lowestOneBit(info.getHeight()));
            if (p < k) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", info.getId(), info.getWidth(), info.getHeight(), MathHelper.log2(k), MathHelper.log2(p));
                k = p;
            }
        }

        int m = Math.min(j, k);
        int n = MathHelper.log2(m);
        if (n < mipmapLevel) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, mipmapLevel, n, m);
            p = n;
        } else {
            p = mipmapLevel;
        }

        profiler.swap("register");
        textureStitcher.add(MissingSprite.getMissingInfo());
        profiler.swap("stitching");

        try {
            textureStitcher.stitch();
        } catch (TextureStitcherCannotFitException var16) {
            CrashReport crashReport = CrashReport.create(var16, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", var16.getSprites().stream().map((infox) -> String.format("%s[%dx%d]", infox.getId(), infox.getWidth(), infox.getHeight())).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", i);
            throw new CrashException(crashReport);
        }

        profiler.swap("loading");

        List<Sprite> list = this.loadSprites(resourceManager, textureStitcher, p);
        profiler.pop();
        cir.setReturnValue(FastSpriteAtlas.dataCreate(set, textureStitcher.getWidth(), textureStitcher.getHeight(), p, list));
    }

    private ArrayList<Sprite> loadSprites(ResourceManager resourceManager, TextureStitcher textureStitcher, int maxLevel) {
        HashMap<String, NativeImage> spriteCache = Dash.spriteCache;
        ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        List<CompletableFuture<?>> list = Lists.newArrayList();
        textureStitcher.getStitchedSprites((info, atlasWidth, atlasHeight, x, y) -> {
            if (info == MissingSprite.getMissingInfo()) {
                MissingSprite missingSprite = MissingSprite.getMissingSprite((SpriteAtlasTexture) (Object) this, maxLevel, atlasWidth, atlasHeight, x, y);
                concurrentLinkedQueue.add(missingSprite);
            } else {
                list.add(CompletableFuture.runAsync(() -> {
                    String key = StringHelper.idToFile(info.getId().toString());
                    Sprite sprite;
                    if (spriteCache.containsKey(key)) {
                        sprite = SpriteAccessor.newSprite((SpriteAtlasTexture) (Object) this, info, maxLevel, atlasWidth, atlasHeight, x, y, spriteCache.get(key));
                    } else {
                        sprite = loadSpriteFast(resourceManager, info, atlasWidth, atlasHeight, maxLevel, x, y);
                    }
                    if (sprite != null) {
                        concurrentLinkedQueue.add(sprite);
                    }

                }, Util.getMainWorkerExecutor()));
            }

        });
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(concurrentLinkedQueue);
    }

    @Nullable
    private Sprite loadSpriteFast(ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) {
        String key = StringHelper.idToFile(info.getId().toString());
        Identifier identifier = this.getTexturePath(info.getId());
        System.out.println("not fast: " + info.getId());
        try {
            Resource resource = container.getResource(identifier);
            Sprite var12;
            try {
                NativeImage nativeImage = NativeImage.read(resource.getInputStream());
                var12 = SpriteAccessor.newSprite((SpriteAtlasTexture) (Object) this, info, maxLevel, atlasWidth, atlasHeight, x, y, nativeImage);
                File file = new File(String.valueOf(Dash.config.resolve("dash/sprite/" + key + ".png")));
                file.createNewFile();
                nativeImage.writeFile(file);
                resource.close();
            } finally {
                if (resource != null) {
                    resource.close();
                }

            }
            return var12;
        } catch (RuntimeException var25) {
            LOGGER.error("Unable to parse metadata from {}", identifier, var25);
            return null;
        } catch (IOException var26) {
            LOGGER.error("Using missing texture, unable to load {}", identifier, var26);
            return null;
        }
    }


}


