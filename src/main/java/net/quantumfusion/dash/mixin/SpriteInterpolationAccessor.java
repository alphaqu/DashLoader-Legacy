package net.quantumfusion.dash.mixin;

import io.netty.util.SuppressForbidden;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Sprite.Interpolation.class)
public interface SpriteInterpolationAccessor {

    @Accessor()
    NativeImage[] getImages();

    @Accessor()
    void setImages(NativeImage[] images);
}
