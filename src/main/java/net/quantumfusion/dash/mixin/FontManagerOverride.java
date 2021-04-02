package net.quantumfusion.dash.mixin;

import net.minecraft.client.font.FontManager;
import net.minecraft.resource.ResourceReloadListener;
import net.quantumfusion.dash.font.FastFontManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontManager.class)
public class FontManagerOverride {

    @Inject(method = "getResourceReloadListener()Lnet/minecraft/resource/ResourceReloadListener;",
            at = @At(value = "HEAD"), cancellable = true)
    private void prepare(CallbackInfoReturnable<ResourceReloadListener> cir) {
        cir.setReturnValue(new FastFontManager((FontManagerAccessor) this).resourceReloadListener);
    }
}
