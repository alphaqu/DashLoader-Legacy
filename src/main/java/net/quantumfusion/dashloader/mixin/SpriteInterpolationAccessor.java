package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Sprite.Interpolation.class)
public interface SpriteInterpolationAccessor {

    @Accessor()
    NativeImage[] getImages();

    @Accessor()
    void setImages(NativeImage[] images);
}
