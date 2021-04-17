package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(SpriteAtlasTexture.class)
public interface SpriteAtlasTextureAccessor {

    @Accessor
    Map<Identifier, Sprite> getSprites();

    @Accessor
    void setSprites(Map<Identifier, Sprite> sprites);

    @Accessor
    List<Sprite> getAnimatedSprites();

    @Accessor
    void setAnimatedSprites(List<Sprite> animatedSprites);

    @Accessor
    Set<Identifier> getSpritesToLoad();

    @Accessor
    void setSpritesToLoad(Set<Identifier> spritesToLoad);

    @Accessor
    Identifier getId();

    @Accessor
    void setId(Identifier id);

    @Accessor
    int getMaxTextureSize();

    @Accessor
    void setMaxTextureSize(int maxTextureSize);

}
