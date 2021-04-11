package net.quantumfusion.dash.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;

import java.util.*;

public class DashSpriteAtlasTexture {
    @Serialize(order = 0)
    public List<DashSprite> animatedSprites;
    @Serialize(order = 1)
    public Set<DashIdentifier> spritesToLoad;
    @Serialize(order = 2)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<DashIdentifier, DashSprite> sprites;
    @Serialize(order = 3)
    public final DashIdentifier id;
    @Serialize(order = 4)
    public final int maxTextureSize;

    public DashSpriteAtlasTexture(@Deserialize("animatedSprites") List<DashSprite> animatedSprites,
                                  @Deserialize("spritesToLoad") Set<DashIdentifier> spritesToLoad,
                                  @Deserialize("sprites") Map<DashIdentifier, DashSprite> sprites,
                                  @Deserialize("id") DashIdentifier id,
                                  @Deserialize("maxTextureSize") int maxTextureSize) {
        this.animatedSprites = animatedSprites;
        this.spritesToLoad = spritesToLoad;
        this.sprites = sprites;
        this.id = id;
        this.maxTextureSize = maxTextureSize;
    }

    public DashSpriteAtlasTexture(SpriteAtlasTexture spriteAtlasTexture) {
        SpriteAtlasTextureAccessor spriteTextureAccess = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
        animatedSprites = new ArrayList<>();
        spritesToLoad = new HashSet<>();
        sprites = new HashMap<>();
        spriteTextureAccess.getAnimatedSprites().forEach(sprite -> animatedSprites.add(new DashSprite(sprite)));
        spriteTextureAccess.getSpritesToLoad().forEach(identifier -> spritesToLoad.add(new DashIdentifier(identifier)));
        spriteTextureAccess.getSprites().forEach((identifier, sprite) -> sprites.put(new DashIdentifier(identifier), new DashSprite(sprite)));
        id = new DashIdentifier(spriteTextureAccess.getId());
        maxTextureSize = spriteTextureAccess.getMaxTextureSize();
    }

    public SpriteAtlasTexture toUndash() {
        try {
            SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture) Dash.getUnsafe().allocateInstance(SpriteAtlasTexture.class);
            SpriteAtlasTextureAccessor spriteAtlasTextureAccessor = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
            Map<Identifier, Sprite> out = new HashMap<>();
            sprites.forEach((dashIdentifier, dashSprite) -> out.put(dashIdentifier.toUndash(), dashSprite.toUndash(spriteAtlasTexture)));
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
