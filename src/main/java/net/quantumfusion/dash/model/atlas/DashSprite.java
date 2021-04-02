package net.quantumfusion.dash.model.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.mixin.SpriteAccessor;

public class DashSprite {

    @Serialize(order = 0)
    public final int maxLevel;
    @Serialize(order = 1)
    public final int atlasWidth;
    @Serialize(order = 2)
    public final int atlasHeight;
    @Serialize(order = 3)
    public final int x;
    @Serialize(order = 4)
    public final int y;
    @Serialize(order = 5)
    public final byte[] image;

    public NativeImage nativeImage;

    //TODO generate images with id to recover later.
    public final long nativeImageID ;

    public DashSprite(@Deserialize("maxLevel") int maxLevel,
                      @Deserialize("atlasWidth") int atlasWidth,
                      @Deserialize("atlasHeight") int atlasHeight,
                      @Deserialize("x") int x,
                      @Deserialize("y") int y,
                      @Deserialize("image") byte[] image
    ) {
        this.maxLevel = maxLevel;
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public DashSprite(Sprite sprite) {
        SpriteAccessor spriteAccessor = ((SpriteAccessor)sprite);
    }


    public void assignImage(NativeImage nativeImage) {
        this.nativeImage = nativeImage;
    }

    public Sprite toUndash(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info) {
        return SpriteAccessor.newSprite(spriteAtlasTexture, info, maxLevel, atlasWidth, atlasHeight, x, y, nativeImage);
    }
}
