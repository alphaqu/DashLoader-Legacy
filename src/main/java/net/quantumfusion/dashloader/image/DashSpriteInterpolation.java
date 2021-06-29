package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.SpriteInterpolationAccessor;
import net.quantumfusion.dashloader.util.Unsafe;
import net.quantumfusion.dashloader.util.duck.SpriteInterpolationDuck;

public class DashSpriteInterpolation {
    @Serialize(order = 0)
    public final int[] images;

    public DashSpriteInterpolation(@Deserialize("images") int[] images) {
        this.images = images;
    }

    public DashSpriteInterpolation(Sprite.Interpolation interpolation, DashRegistry registry) {
        final NativeImage[] images = ((SpriteInterpolationAccessor) (Object) interpolation).getImages();
        this.images = new int[images.length];
        for (int i = 0, imagesLength = images.length; i < imagesLength; i++) {
            this.images[i] = registry.createImagePointer(images[i]);
        }

    }

    public final Sprite.Interpolation toUndash(final Sprite owner, final DashRegistry registry) {
        final Sprite.Interpolation spriteInterpolation = Unsafe.allocateInstance(Sprite.Interpolation.class);
        final SpriteInterpolationAccessor spriteInterpolationAccessor = ((SpriteInterpolationAccessor) (Object) spriteInterpolation);
        final NativeImage[] nativeImages = new NativeImage[images.length];
        for (int i = 0, imagesLength = images.length; i < imagesLength; i++) {
            nativeImages[i] = registry.getImage(images[i]);
        }
        spriteInterpolationAccessor.setImages(nativeImages);
        ((SpriteInterpolationDuck) (Object) spriteInterpolation).interpolation(owner);
        return spriteInterpolation;
    }
}
