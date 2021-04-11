package net.quantumfusion.dash.mixin;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(ResourceTexture.TextureData.class)
public class ResourceTextureMixin {
    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/ResourceTexture$TextureData;", at = @At(value = "HEAD"), cancellable = true)
    private static void loadFast(ResourceManager resourceManager, Identifier identifier, CallbackInfoReturnable<ResourceTexture.TextureData> cir) {
        try {
            Resource resource = resourceManager.getResource(identifier);
            ResourceTexture.TextureData var6;
            try {
                var6 = new ResourceTexture.TextureData(resource.getMetadata(TextureResourceMetadata.READER), NativeImage.read(resource.getInputStream()));
                System.out.println(var6.getImage().getFormat());

            } finally {
                if (resource != null) {
                    resource.close();
                }
            }
            cir.setReturnValue(var6);
        } catch (IOException var20) {
            cir.setReturnValue(new ResourceTexture.TextureData(var20));
        }
    }

}
