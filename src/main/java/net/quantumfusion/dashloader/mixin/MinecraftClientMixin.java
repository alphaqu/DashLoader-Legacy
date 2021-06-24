package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.mixin.accessor.MinecraftClientAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;reloadResources(Z)Ljava/util/concurrent/CompletableFuture;"), cancellable = true)
    private void reloadResourcesOverride(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        DashLoader.getInstance().requestReload();
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void showDaScreen(RunArgs args, CallbackInfo ci) {
        ((MinecraftClientAccessor) this).callRender(false);
    }


}
