package net.quantumfusion.dash.mixin;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasTextureData;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "upload(Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;)V",
    at = @At(value = "HEAD"))
    private void saveAtlasInfo(SpriteAtlasTexture.Data data, CallbackInfo ci) {
        Dash.loader.atlasData.put((SpriteAtlasTexture)(Object)this,new DashSpriteAtlasTextureData(data));
    }
}
