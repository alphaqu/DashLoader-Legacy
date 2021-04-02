package net.quantumfusion.dash.model.atlas;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DashSpriteAtlasTexture {
    public final List<DashSprite> animatedSprites;
    public final Set<DashIdentifier> spritesToLoad;
    public final Map<DashIdentifier, DashSprite> sprites;
    public final DashIdentifier id;
    public final int maxTextureSize;

    public DashSpriteAtlasTexture(List<DashSprite> animatedSprites, Set<DashIdentifier> spritesToLoad, Map<DashIdentifier, DashSprite> sprites, DashIdentifier id, int maxTextureSize) {
        this.animatedSprites = animatedSprites;
        this.spritesToLoad = spritesToLoad;
        this.sprites = sprites;
        this.id = id;
        this.maxTextureSize = maxTextureSize;
    }

    public DashSpriteAtlasTexture(SpriteAtlasTexture spriteAtlasTexture) {
        SpriteAtlasTextureAccessor spriteTextureAccess = ((SpriteAtlasTextureAccessor)spriteAtlasTexture);
        List<DashSprite> animatedSpritesOut = new ArrayList<>();
        spriteTextureAccess.getAnimatedSprites().forEach(sprite -> animatedSpritesOut.add(new DashSprite(sprite)));
        animatedSprites = spriteTextureAccess.getAnimatedSprites()
    }
}
