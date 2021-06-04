package net.quantumfusion.dashloader.mixin.accessor;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sprite.class)
public interface SpriteAccessor {

    @Invoker("<init>")
    static Sprite newSprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
        throw new AssertionError();
    }

    @Accessor
    @Mutable
    void setAtlas(SpriteAtlasTexture atlas);

    @Accessor
    @Mutable
    void setId(Identifier id);

    @Accessor
    Sprite.Animation getAnimation();

    @Accessor
    @Mutable
    void setAnimation(Sprite.Animation animation);

    @Accessor("images")
    NativeImage[] getImages();

    @Accessor("images")
    @Mutable
    void setImages(NativeImage[] images);

    @Accessor("x")
    int getX();

    @Accessor("x")
    @Mutable
    void setX(int x);

    @Accessor("y")
    int getY();

    @Accessor("y")
    @Mutable
    void setY(int y);

    @Accessor("uMin")
    float getUMin();

    @Accessor("uMin")
    @Mutable
    void setUMin(float uMin);

    @Accessor("uMax")
    float getUMax();

    @Accessor("uMax")
    @Mutable
    void setUMax(float uMax);

    @Accessor("vMin")
    float getVMin();

    @Accessor("vMin")
    @Mutable
    void setVMin(float vMin);

    @Accessor("vMax")
    float getVMax();

    @Accessor("vMax")
    @Mutable
    void setVMax(float vMax);


    @Accessor("width")
    @Mutable
    void setWidth(int width);

    @Accessor("height")
    @Mutable
    void setHeight(int height);

}
