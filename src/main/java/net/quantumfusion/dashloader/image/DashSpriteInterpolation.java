package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.SpriteInterpolationAccessor;
import net.quantumfusion.dashloader.util.duck.SpriteInterpolationDuck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashSpriteInterpolation {
    @Serialize(order = 0)
    public final List<Integer> images;

    public DashSpriteInterpolation(@Deserialize("images") List<Integer> images) {
        this.images = images;
    }

    public DashSpriteInterpolation(Sprite.Interpolation interpolation, DashRegistry registry) {
        images = new ArrayList<>();
        Arrays.stream(((SpriteInterpolationAccessor) (Object) interpolation).getImages()).forEach(nativeImage -> images.add(registry.createImagePointer(nativeImage)));

    }

    public final Sprite.Interpolation toUndash(final Sprite owner, final DashRegistry registry) {
        final Sprite.Interpolation spriteInterpolation = Unsafe.allocateInstance(Sprite.Interpolation.class);
        final SpriteInterpolationAccessor spriteInterpolationAccessor = ((SpriteInterpolationAccessor) (Object) spriteInterpolation);
        final List<NativeImage> nativeImages = new ArrayList<>();
        images.forEach(dashImage -> nativeImages.add(registry.getImage(dashImage)));
        spriteInterpolationAccessor.setImages(nativeImages.toArray(new NativeImage[0]));
        ((SpriteInterpolationDuck) (Object) spriteInterpolation).interpolation(owner);
        return spriteInterpolation;
    }
}
