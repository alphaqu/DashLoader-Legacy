package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(DefaultClientResourcePack.class)
public class DefaultClientResourcePackMixin {

    @Shadow
    @Final
    private ResourceIndex index;

    @Inject(method = "contains(Lnet/minecraft/resource/ResourceType;Lnet/minecraft/util/Identifier;)Z",
            at = @At(value = "HEAD"), cancellable = true)
    private void prepare(ResourceType type, Identifier id, CallbackInfoReturnable<Boolean> cir) {
        if (type == ResourceType.CLIENT_RESOURCES) {
            File file = this.index.getResource(id);
            if (file != null && file.exists()) {
                cir.setReturnValue(true);
            }
        }
    }
}
