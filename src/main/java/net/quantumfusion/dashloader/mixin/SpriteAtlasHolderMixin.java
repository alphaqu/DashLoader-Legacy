package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashCacheState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SpriteAtlasHolder.class)
public class SpriteAtlasHolderMixin {

    @Shadow
    @Final
    private SpriteAtlasTexture atlas;

    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;",
            at = @At(value = "HEAD"), cancellable = true)
    private void prepareOverride(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir) {
        if (DashLoader.getInstance().state == DashCacheState.LOADED) {
            cir.setReturnValue(null);
        }
    }


    @Inject(method = "apply(Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void applyOverride(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        if (DashLoader.getInstance().state == DashCacheState.LOADED) {
            ci.cancel();
        }
    }

    @Inject(method = "apply(Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "TAIL"), cancellable = true)
    private void applyCreate(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        if (DashLoader.getInstance().state == DashCacheState.LOADED) {
            ci.cancel();
        } else {
            System.out.println(atlas.getId());
            DashLoader.getInstance().addExtraAtlasAssets(atlas);
        }
    }
}
