package net.quantumfusion.dash.mixin;

import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashCache;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Final
    private static CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;
    @Shadow
    @Nullable
    public Overlay overlay;
    @Shadow
    @Final
    public WorldRenderer worldRenderer;
    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;
    @Shadow
    @Nullable
    private CompletableFuture<Void> resourceReloadFuture;
    @Shadow
    @Final
    private ResourcePackManager resourcePackManager;
    @Shadow
    @Final
    private BakedModelManager bakedModelManager;

    @Shadow
    public abstract void setOverlay(@Nullable Overlay overlay);

    @Shadow
    protected abstract void handleResourceReloadException(Throwable throwable);

    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "HEAD"), cancellable = true)
    private void reloadResourcesOverride(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (this.resourceReloadFuture != null) {
            cir.setReturnValue(resourceReloadFuture);
        } else {
            CompletableFuture<Void> completableFuture = new CompletableFuture();
            if (this.overlay instanceof SplashScreen) {
                this.resourceReloadFuture = completableFuture;
            } else {
                this.resourcePackManager.scanPacks();
                List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
                this.setOverlay(new SplashScreen((MinecraftClient) (Object) this, this.resourceManager.beginMonitoredReload(Util.getMainWorkerExecutor(), (MinecraftClient) (Object) this, COMPLETED_UNIT_FUTURE, list), (optional) -> {
                    Util.ifPresentOrElse(optional, this::handleResourceReloadException, () -> {
                        this.worldRenderer.reload();
                        completableFuture.complete(null);
                    });
                }, true));
            }
            cir.setReturnValue(completableFuture);
        }
    }

}
