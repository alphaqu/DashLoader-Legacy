package net.quantumfusion.dashloader.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.SpriteAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashSprite {
    @Serialize(order = 0)
    public final DashSpriteInfo info;
    @Serialize(order = 1)
    public final DashAnimationResourceMetadata animationMetadata;
    @Serialize(order = 2)
    public final int[] frameXs;
    @Serialize(order = 3)
    public final int[] frameYs;
    @Serialize(order = 4)
    @SerializeNullable
    @Nullable
    public final DashSpriteInterpolation interpolation;
    @Serialize(order = 5)
    public final int x;
    @Serialize(order = 6)
    public final int y;
    @Serialize(order = 7)
    public final float uMin;
    @Serialize(order = 8)
    public final float uMax;
    @Serialize(order = 9)
    public final float vMin;
    @Serialize(order = 10)
    public final float vMax;
    @Serialize(order = 11)
    public final int frameIndex;
    @Serialize(order = 12)
    public final int frameTicks;
    @Serialize(order = 13)
    public List<Long> images;


    public DashSprite(@Deserialize("info") DashSpriteInfo info,
                      @Deserialize("animationMetadata") DashAnimationResourceMetadata animationMetadata,
                      @Deserialize("frameXs") int[] frameXs,
                      @Deserialize("frameYs") int[] frameYs,
                      @Deserialize("interpolation") @Nullable DashSpriteInterpolation interpolation,
                      @Deserialize("x") int x,
                      @Deserialize("y") int y,
                      @Deserialize("uMin") float uMin,
                      @Deserialize("uMax") float uMax,
                      @Deserialize("vMin") float vMin,
                      @Deserialize("vMax") float vMax,
                      @Deserialize("frameIndex") int frameIndex,
                      @Deserialize("frameTicks") int frameTicks,
                      @Deserialize("images") List<Long> images
                      ) {
        this.info = info;
        this.animationMetadata = animationMetadata;
        this.images = images;
        this.frameXs = frameXs;
        this.frameYs = frameYs;
        this.interpolation = interpolation;
        this.x = x;
        this.y = y;
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
        this.frameIndex = frameIndex;
        this.frameTicks = frameTicks;
    }

    public DashSprite(Sprite sprite, DashRegistry registry) {
        SpriteAccessor spriteAccess = ((SpriteAccessor) sprite);
        info = new DashSpriteInfo(spriteAccess.getInfo(),registry);
        animationMetadata = new DashAnimationResourceMetadata(spriteAccess.getAnimationMetadata());
        images = new ArrayList<>();
        Arrays.stream(spriteAccess.getImages()).forEach(nativeImage -> images.add(registry.createImagePointer(nativeImage)));
        frameXs = spriteAccess.getFrameXs();
        frameYs = spriteAccess.getFrameYs();
        Sprite.Interpolation interpolation = spriteAccess.getInterpolation();
        if (interpolation != null) {
            this.interpolation = new DashSpriteInterpolation(spriteAccess.getInterpolation());
        } else {
            this.interpolation = null;
        }
        x = spriteAccess.getX();
        y = spriteAccess.getY();
        uMin = spriteAccess.getUMin();
        uMax = spriteAccess.getUMax();
        vMin = spriteAccess.getVMin();
        vMax = spriteAccess.getVMax();
        frameIndex = spriteAccess.getFrameIndex();
        frameTicks = spriteAccess.getFrameTicks();
    }

    public Sprite toUndash(DashRegistry registry) {
        Sprite out = Unsafe.allocateInstance(Sprite.class);
        SpriteAccessor spriteAccessor = ((SpriteAccessor) out);
        spriteAccessor.setInfo(info.toUndash(registry));
        spriteAccessor.setAnimationMetadata(animationMetadata.toUndash());
        ArrayList<NativeImage> imagesOut = new ArrayList<>();
        images.forEach(dashImage -> imagesOut.add(registry.getImage(dashImage)));
        spriteAccessor.setImages(imagesOut.toArray(new NativeImage[0]));
        spriteAccessor.setFrameXs(frameXs);
        spriteAccessor.setFrameYs(frameYs);
        if (interpolation != null) {
            spriteAccessor.setInterpolation(interpolation.toUndash(out));
        } else {
            spriteAccessor.setInterpolation(null);
        }
        spriteAccessor.setX(x);
        spriteAccessor.setY(y);
        spriteAccessor.setUMin(uMin);
        spriteAccessor.setUMax(uMax);
        spriteAccessor.setVMin(vMin);
        spriteAccessor.setVMax(vMax);
        spriteAccessor.setFrameIndex(frameIndex);
        spriteAccessor.setFrameTicks(frameTicks);
        return out;
    }


}
