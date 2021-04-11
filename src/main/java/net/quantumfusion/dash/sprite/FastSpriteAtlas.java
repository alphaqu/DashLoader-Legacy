package net.quantumfusion.dash.sprite;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.activej.serializer.stream.StreamOutput;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.util.StringHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FastSpriteAtlas {

    public static Collection<Sprite.Info> loadSprites(ResourceManager resourceManager, Set<Identifier> ids, Identifier id) {
        HashMap<Identifier, ArrayList<Sprite.Info>> spriteInfoCache = Dash.spriteInfoCache;
        if (spriteInfoCache.containsKey(id)) {
            return spriteInfoCache.get(id);
        } else {
            List<CompletableFuture<?>> list = Lists.newArrayList();
            ConcurrentLinkedQueue<Sprite.Info> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
            for (Identifier identifier : ids) {
                if (!MissingSprite.getMissingSpriteId().equals(identifier)) {
                    list.add(CompletableFuture.runAsync(() -> {
                        Identifier identifier2 = getTexturePath(identifier);
                        Sprite.Info info3;
                        try {
                            try (Resource resource = resourceManager.getResource(identifier2)) {
                                PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
                                AnimationResourceMetadata animationResourceMetadata;
                                animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
                                if (animationResourceMetadata == null) {
                                    animationResourceMetadata = AnimationResourceMetadata.EMPTY;
                                }
                                System.out.println(identifier);
                                Pair<Integer, Integer> pair = animationResourceMetadata.method_24141(pngFile.width, pngFile.height);
                                info3 = new Sprite.Info(identifier, pair.getFirst(), pair.getSecond(), animationResourceMetadata);
                            }
                        } catch (RuntimeException | IOException var22) {
                            return;
                        }

                        concurrentLinkedQueue.add(info3);
                    }, Util.getMainWorkerExecutor()));
                }
            }

            CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
            ArrayList<Sprite.Info> out = new ArrayList<>(concurrentLinkedQueue);
            SpriteInfoCache info = SpriteInfoCache.create(out, id);
            try {
                StreamOutput streamOutput = StreamOutput.create(
                        Files.newOutputStream(Dash.config.resolve("dash/sprite/info/" + StringHelper.idToFile(id.toString()) + ".activej"),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.WRITE
                        ));
                streamOutput.serialize(Dash.spriteSerializer, info);
                streamOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return concurrentLinkedQueue;
        }
    }

    private static Identifier getTexturePath(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), String.format("textures/%s%s", identifier.getPath(), ".png"));
    }

    public static SpriteAtlasTexture.Data dataCreate(Set<Identifier> spriteIds, int width, int height, int maxLevel, List<Sprite> sprites) {
        return new SpriteAtlasTexture.Data(spriteIds, width, height, maxLevel, sprites);
    }
}

