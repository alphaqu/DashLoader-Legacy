package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.DashLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Shadow
    @Final
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> spriteAwareFactories;

    @Shadow
    protected abstract void loadTextureList(ResourceManager resourceManager, Identifier id, Map<Identifier, List<Identifier>> result);

    @Shadow
    @Final
    private SpriteAtlasTexture particleAtlasTexture;

    @Shadow
    @Final
    private Map<ParticleTextureSheet, Queue<Particle>> particles;

    /**
     * @author alphaqu, leocth
     * @reason DashLoader needs to replace the resource loading process completely. Thus, an overwrite is needed.
     */
    //TODO: consider splitting this into two injects instead of copying the entire damn thing
    /*

    @Overwrite
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        if (DashLoader.getInstance().getParticlesOut() != null) {
            LogManager.getLogger().info("Particles loading");
            return CompletableFuture.runAsync(
                () -> DashLoader.getInstance()
                        .getParticlesOut()
                        .forEach((identifier, sprites) ->
                            spriteAwareFactories.get(identifier).setSprites(sprites)
                        )
            ).thenCompose(synchronizer::whenPrepared);
        } else {
            Map<Identifier, List<Identifier>> map = Maps.newConcurrentMap();

            var completableFutures=
                Registry.PARTICLE_TYPE.getIds()
                    .stream()
                    .map(id ->
                        CompletableFuture.runAsync(
                            () -> this.loadTextureList(manager, id, map),
                            prepareExecutor
                        )
                    )
                    .toArray(CompletableFuture<?>[]::new);

            CompletableFuture<?> var10000 = CompletableFuture.allOf(completableFutures).thenApplyAsync(fut -> {
                prepareProfiler.startTick();
                prepareProfiler.push("stitching");
                SpriteAtlasTexture.Data data = this.particleAtlasTexture.stitch(manager, map.values().stream().flatMap(Collection::stream), prepareProfiler, 0);
                prepareProfiler.pop();
                prepareProfiler.endTick();
                return data;
            }, prepareExecutor);

            cir.setReturnValue(var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((data) -> {
                LogManager.getLogger().info("Particle Apply");
                this.particles.clear();
                applyProfiler.startTick();
                applyProfiler.push("upload");
                this.particleAtlasTexture.upload((SpriteAtlasTexture.Data) data);
                applyProfiler.swap("bindSpriteSets");
                Sprite sprite = this.particleAtlasTexture.getSprite(MissingSprite.getMissingSpriteId());
                map.forEach((identifier, spritesAssets) -> {
                    ImmutableList<Sprite> spriteList;
                    if (spritesAssets.isEmpty()) {
                        spriteList = ImmutableList.of(sprite);
                    } else {
                        Stream<Identifier> spriteStream = spritesAssets.stream();
                        SpriteAtlasTexture spriteAtlasTexture = this.particleAtlasTexture;
                        spriteList = spriteStream.map(spriteAtlasTexture::getSprite).collect(ImmutableList.toImmutableList());
                    }
                    ImmutableList<Sprite> immutableList = spriteList;
                    ((ParticleManagerSimpleSpriteProviderAccessor) this.spriteAwareFactories.get(identifier)).setSprites(immutableList);
                });



                applyProfiler.pop();
                applyProfiler.endTick();
            }, applyExecutor));
        }
        cir.cancel();
    }
    */

    @Inject(
        method = "reload",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onLambdaThenAcceptAsync(
        ResourceReloader.Synchronizer synchronizer,
        ResourceManager manager,
        Profiler prepareProfiler,
        Profiler applyProfiler,
        Executor prepareExecutor,
        Executor applyExecutor,
        CallbackInfoReturnable<CompletableFuture<Void>> cir
    ) {
        final var dashLoader = DashLoader.getInstance();
        if (dashLoader.getParticlesOut() != null) {
            DashLoader.LOGGER.info("Particles loading");
            cir.setReturnValue(
                CompletableFuture.runAsync(
                    () -> dashLoader
                        .getParticlesOut()
                        .forEach((identifier, sprites) ->
                            spriteAwareFactories.get(identifier).setSprites(sprites)
                        )
                ).thenCompose(synchronizer::whenPrepared)
            );
        }
    }

    @SuppressWarnings("UnresolvedMixinReference") // MCDev doesn't recognize synthetic methods lol
    @Inject(
        method = "method_18831",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
            ordinal = 0
        )
    )
    private void onLambdaThenAcceptAsync(
        Profiler profiler,
        Map<Identifier, List<Identifier>> idsMap,
        SpriteAtlasTexture.Data data,
        CallbackInfo ci
    ) {
        DashLoader.getInstance().addParticleManagerAssets(spriteAwareFactories, particleAtlasTexture);
    }
}
