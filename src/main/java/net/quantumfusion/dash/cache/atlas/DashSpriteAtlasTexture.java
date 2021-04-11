package net.quantumfusion.dash.cache.atlas;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;

import java.util.*;

public class DashSpriteAtlasTexture {
    public List<DashSprite> animatedSprites;
    public Set<DashIdentifier> spritesToLoad;
    public Map<DashIdentifier, DashSprite> sprites;
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
        animatedSprites = new ArrayList<>();
        spritesToLoad = new HashSet<>();
        sprites = new HashMap<>();
        spriteTextureAccess.getAnimatedSprites().forEach(sprite -> animatedSprites.add(new DashSprite(sprite)));
        spriteTextureAccess.getSpritesToLoad().forEach(identifier -> spritesToLoad.add(new DashIdentifier(identifier)));
        spriteTextureAccess.getSprites().forEach((identifier, sprite) -> sprites.put(new DashIdentifier(identifier),new DashSprite(sprite)));
        id = new DashIdentifier(spriteTextureAccess.getId());
        maxTextureSize = spriteTextureAccess.getMaxTextureSize();
    }

    public SpriteAtlasTexture toUndash() {
        try {
            SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture) Dash.getUnsafe().allocateInstance(SpriteAtlasTexture.class);
            SpriteAtlasTextureAccessor spriteAtlasTextureAccessor = ((SpriteAtlasTextureAccessor)spriteAtlasTexture);
            Map<Identifier, Sprite> out = new HashMap<>();
            sprites.forEach((dashIdentifier, dashSprite) -> out.put(dashIdentifier.toUndash(),dashSprite.toUndash(spriteAtlasTexture)));
            Set<Identifier> outLoad = new HashSet<>();
            spritesToLoad.forEach(dashIdentifier -> outLoad.add(dashIdentifier.toUndash()));

            List<Sprite> outAnimatedSprites = new ArrayList<>();
            animatedSprites.forEach(dashSprite -> outAnimatedSprites.add(dashSprite.toUndash(spriteAtlasTexture)));
            spriteAtlasTextureAccessor.setAnimatedSprites(outAnimatedSprites);
            spriteAtlasTextureAccessor.setSpritesToLoad(outLoad);
            spriteAtlasTextureAccessor.setSprites(out);
            spriteAtlasTextureAccessor.setId(id.toUndash());
            spriteAtlasTextureAccessor.setMaxTextureSize(maxTextureSize);
            return spriteAtlasTexture;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }


}
