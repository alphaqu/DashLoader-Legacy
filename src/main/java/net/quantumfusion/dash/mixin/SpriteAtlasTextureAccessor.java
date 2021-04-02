package net.quantumfusion.dash.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

    @Accessor("sprites")
    Map<Identifier, Sprite> getSprites();

    @Accessor("animatedSprites")
    List<Sprite> getAnimatedSprites();

    @Accessor("spritesToLoad")
    Set<Identifier> getSpritesToLoad();

    @Accessor("id")
    Identifier getId();

    @Accessor("maxTextureSize")
    int getMaxTextureSize();
}
