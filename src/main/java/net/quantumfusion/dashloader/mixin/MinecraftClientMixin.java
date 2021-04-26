package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.quantumfusion.dashloader.DashLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "HEAD"), cancellable = true)
    private void reloadResourcesOverride(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        DashLoader loader = new DashLoader();
        loader.destroyCache();
    }

}
