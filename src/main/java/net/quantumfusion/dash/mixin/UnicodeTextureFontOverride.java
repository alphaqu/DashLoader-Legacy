package net.quantumfusion.dash.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.font.fonts.UnicodeFont;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(UnicodeTextureFont.Loader.class)
public class UnicodeTextureFontOverride {

    @Shadow
    @Final
    private Identifier sizes;

    @Shadow
    @Final
    private String template;

    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;)Lnet/minecraft/client/font/Font;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void fastLoad(ResourceManager manager, CallbackInfoReturnable<Font> cir) {
        try {
            System.out.println(sizes);
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);
            UnicodeFont var5;
            try {
                byte[] bs = new byte[65536];
                resource.getInputStream().read(bs);
                var5 = new UnicodeFont(manager, bs);
            } finally {
                if (resource != null) {
                    resource.close();
                }

            }
            cir.setReturnValue(var5);
        } catch (IOException var17) {
            cir.setReturnValue(null);
        }

    }
}
