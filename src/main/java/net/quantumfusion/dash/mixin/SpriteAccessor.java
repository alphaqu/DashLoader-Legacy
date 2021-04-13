package net.quantumfusion.dash.mixin;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sprite.class)
public interface SpriteAccessor {

    @Invoker("<init>")
    static Sprite newSprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
        throw new AssertionError();
    }

    @Accessor
    SpriteAtlasTexture getAtlas();

    @Accessor
    void setAtlas(SpriteAtlasTexture atlas);

    @Accessor("info")
    Sprite.Info getInfo();

    @Accessor("info")
    void setInfo(Sprite.Info info);

    @Accessor("animationMetadata")
    AnimationResourceMetadata getAnimationMetadata();

    @Accessor("animationMetadata")
    void setAnimationMetadata(AnimationResourceMetadata animationMetadata);

    @Accessor("images")
    NativeImage[] getImages();

    @Accessor("images")
    void setImages(NativeImage[] images);

    @Accessor("frameXs")
    int[] getFrameXs();

    @Accessor("frameXs")
    void setFrameXs(int[] frameXs);

    @Accessor("frameYs")
    int[] getFrameYs();

    @Accessor("frameYs")
    void setFrameYs(int[] frameYs);

    @Accessor("interpolation")
    Sprite.Interpolation getInterpolation();

    @Accessor("interpolation")
    void setInterpolation(Sprite.Interpolation interpolation);

    @Accessor("x")
    int getX();

    @Accessor("x")
    void setX(int x);

    @Accessor("y")
    int getY();

    @Accessor("y")
    void setY(int y);

    @Accessor("uMin")
    float getUMin();

    @Accessor("uMin")
    void setUMin(float uMin);

    @Accessor("uMax")
    float getUMax();

    @Accessor("uMax")
    void setUMax(float uMax);

    @Accessor("vMin")
    float getVMin();

    @Accessor("vMin")
    void setVMin(float vMin);

    @Accessor("vMax")
    float getVMax();

    @Accessor("vMax")
    void setVMax(float vMax);

    @Accessor("frameIndex")
    int getFrameIndex();

    @Accessor("frameIndex")
    void setFrameIndex(int frameIndex);

    @Accessor("frameTicks")
    int getFrameTicks();

    @Accessor("frameTicks")
    void setFrameTicks(int frameTicks);
}
