package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashCacheState;
import net.quantumfusion.dashloader.cache.font.FastFontManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(FontManager.class)
public class FontManagerOverride {

    @Inject(method = "getResourceReloadListener()Lnet/minecraft/resource/ResourceReloadListener;",
            at = @At(value = "HEAD"), cancellable = true)
    private void Override(CallbackInfoReturnable<ResourceReloadListener> cir) {
        cir.setReturnValue(new FastFontManager((FontManagerAccessor)this).resourceReloadListener);
        cir.cancel();
    }


}
