package net.quantumfusion.dashloader.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.SpriteAccessor;
import net.quantumfusion.dashloader.util.Dashable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashSprite implements Dashable {
    @Serialize(order = 0)
    @SerializeNullable
    public final DashSpriteAnimation animation;
    @Serialize(order = 1)
    public final int x;
    @Serialize(order = 2)
    public final int y;
    @Serialize(order = 3)
    public final float uMin;
    @Serialize(order = 4)
    public final float uMax;
    @Serialize(order = 5)
    public final float vMin;
    @Serialize(order = 6)
    public final float vMax;
    @Serialize(order = 7)
    public List<Long> images;


    public DashSprite(@Deserialize("animation") DashSpriteAnimation animation,
                      @Deserialize("x") int x,
                      @Deserialize("y") int y,
                      @Deserialize("uMin") float uMin,
                      @Deserialize("uMax") float uMax,
                      @Deserialize("vMin") float vMin,
                      @Deserialize("vMax") float vMax,
                      @Deserialize("images") List<Long> images
    ) {
        this.animation = animation;
        this.images = images;
        this.x = x;
        this.y = y;
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public DashSprite(Sprite sprite, DashRegistry registry) {
        SpriteAccessor spriteAccess = ((SpriteAccessor) sprite);
        images = new ArrayList<>();
        Arrays.stream(spriteAccess.getImages()).forEach(nativeImage -> images.add(registry.createImagePointer(nativeImage)));
        x = spriteAccess.getX();
        y = spriteAccess.getY();
        uMin = spriteAccess.getUMin();
        uMax = spriteAccess.getUMax();
        vMin = spriteAccess.getVMin();
        vMax = spriteAccess.getVMax();
        final Sprite.Animation animation = spriteAccess.getAnimation();
        this.animation = animation == null ? null : new DashSpriteAnimation(animation, registry);
    }

    public final Sprite toUndash(final DashRegistry registry) {
        final Sprite out = Unsafe.allocateInstance(Sprite.class);
        final SpriteAccessor spriteAccessor = ((SpriteAccessor) out);
        final ArrayList<NativeImage> imagesOut = new ArrayList<>();
        images.forEach(dashImage -> imagesOut.add(registry.getImage(dashImage)));
        spriteAccessor.setImages(imagesOut.toArray(new NativeImage[0]));
        spriteAccessor.setX(x);
        spriteAccessor.setY(y);
        spriteAccessor.setUMin(uMin);
        spriteAccessor.setUMax(uMax);
        spriteAccessor.setVMin(vMin);
        spriteAccessor.setVMax(vMax);
        spriteAccessor.setAnimation(animation == null ? null : animation.toUndash(out, registry));
        return out;
    }


}
