package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.util.duck.SpriteInterpolationDuck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Sprite.Interpolation.class)
public class SpriteInterpolationMixin implements SpriteInterpolationDuck {

    @Mutable
    @Shadow
    @Final
    private final NativeImage[] images;

    @SuppressWarnings("ShadowTarget")
    @Shadow
    @Final
    @Mutable
    private Sprite field_21757;


    public SpriteInterpolationMixin(NativeImage[] images) {
        this.images = images;
    }

    @Override
    public void interpolation(Sprite owner) {
        field_21757 = owner;
    }
}
