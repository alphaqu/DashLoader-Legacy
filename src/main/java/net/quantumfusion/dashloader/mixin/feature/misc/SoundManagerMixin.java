package net.quantumfusion.dashloader.mixin.feature.misc;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerMixin {


    @Inject(method = "isSoundResourcePresent(Lnet/minecraft/client/sound/Sound;Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/ResourceManager;)Z",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void dontCheckIfExists(Sound sound, Identifier identifier, ResourceManager resourceManager, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
