package net.quantumfusion.dashloader.mixin.feature.cache;

import net.minecraft.client.font.FontManager;
import net.minecraft.resource.ResourceReloader;
import net.quantumfusion.dashloader.font.FastFontManager;
import net.quantumfusion.dashloader.mixin.accessor.FontManagerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontManager.class)
public class FontManagerOverride {

    @Inject(method = "getResourceReloadListener()Lnet/minecraft/resource/ResourceReloader;",
            at = @At(value = "HEAD"), cancellable = true)
    private void override(CallbackInfoReturnable<ResourceReloader> cir) {
        cir.setReturnValue(new FastFontManager((FontManagerAccessor) this).resourceReloadListener);
        cir.cancel();
    }


}
