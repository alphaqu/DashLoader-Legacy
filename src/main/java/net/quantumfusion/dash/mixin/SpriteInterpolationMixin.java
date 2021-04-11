package net.quantumfusion.dash.mixin;

import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.texture.Sprite$Interpolation")
public class SpriteInterpolationMixin {

    @Mutable
    @Shadow
    @Final
    private final NativeImage[] images;

    public SpriteInterpolationMixin(NativeImage[] images) {
        this.images = images;
    }
}
