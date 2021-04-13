package net.quantumfusion.dash.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.mixin.SpriteInterpolationAccessor;
import net.quantumfusion.dash.util.duck.SpriteInterpolationDuck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DashSpriteInterpolation {
    @Serialize(order = 0)
    public final List<DashImage> images;

    public DashSpriteInterpolation(@Deserialize("images") List<DashImage> images) {
        this.images = images;
    }

    public DashSpriteInterpolation(Sprite.Interpolation interpolation) {
        NativeImage[] images = ((SpriteInterpolationAccessor) (Object) interpolation).getImages();

        this.images = Stream.of(images).map(DashImage::new).collect(Collectors.toList());
    }

    public Sprite.Interpolation toUndash(Sprite owner) {
        try {
            Sprite.Interpolation spriteInterpolation = (Sprite.Interpolation) Dash.getUnsafe().allocateInstance(Sprite.Interpolation.class);
            SpriteInterpolationAccessor spriteInterpolationAccessor = ((SpriteInterpolationAccessor) (Object) spriteInterpolation);
            List<NativeImage> nativeImages = new ArrayList<>();
            images.forEach(dashImage -> nativeImages.add(dashImage.toUndash()));
            spriteInterpolationAccessor.setImages(nativeImages.toArray(new NativeImage[0]));
            ((SpriteInterpolationDuck)(Object)spriteInterpolation).interpolation(owner);
            return spriteInterpolation;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
