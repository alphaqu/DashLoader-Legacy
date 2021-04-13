package net.quantumfusion.dash.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.mixin.AbstractTextureAccessor;
import net.quantumfusion.dash.mixin.SpriteAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;

import java.util.*;

public class DashSpriteAtlasTexture {
    @Serialize(order = 0)
    public List<Integer> animatedSprites;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<DashIdentifier, Integer> sprites;

    @Serialize(order = 2)
    public final DashIdentifier id;
    @Serialize(order = 3)
    public final int maxTextureSize;

    @Serialize(order = 4)
    public boolean bilinear;
    @Serialize(order = 5)
    public boolean mipmap;

    @Serialize(order = 6)
    public DashSpriteAtlasTextureData data;



    public DashSpriteAtlasTexture(@Deserialize("animatedSprites") List<Integer> animatedSprites,
                                  @Deserialize("sprites") Map<DashIdentifier, Integer> sprites,
                                  @Deserialize("id") DashIdentifier id,
                                  @Deserialize("maxTextureSize") int maxTextureSize,
                                  @Deserialize("bilinear") boolean bilinear,
                                  @Deserialize("mipmap") boolean mipmap,
                                  @Deserialize("data") DashSpriteAtlasTextureData data

    ) {
        this.animatedSprites = animatedSprites;
        this.sprites = sprites;
        this.id = id;
        this.maxTextureSize = maxTextureSize;
        this.bilinear = bilinear;
        this.mipmap = mipmap;
        this.data = data;
    }

    public DashSpriteAtlasTexture(SpriteAtlasTexture spriteAtlasTexture, DashSpriteAtlasTextureData data, DashModelLoader loader) {
        SpriteAtlasTextureAccessor spriteTextureAccess = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
        this.data = data;
        animatedSprites = new ArrayList<>();
        sprites = new HashMap<>();
        spriteTextureAccess.getAnimatedSprites().forEach(sprite -> animatedSprites.add(loader.registry.createSpritePointer(sprite)));
        spriteTextureAccess.getSprites().forEach((identifier, sprite) -> sprites.put(new DashIdentifier(identifier), loader.registry.createSpritePointer(sprite)));
        id = new DashIdentifier(spriteTextureAccess.getId());
        maxTextureSize = spriteTextureAccess.getMaxTextureSize();
        bilinear = ((AbstractTextureAccessor)spriteAtlasTexture).getBilinear();
        mipmap = ((AbstractTextureAccessor)spriteAtlasTexture).getMipmap();
    }

    public SpriteAtlasTexture toUndash(DashModelLoader loader) {
        try {
            SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture) Dash.getUnsafe().allocateInstance(SpriteAtlasTexture.class);
            AbstractTextureAccessor access = ((AbstractTextureAccessor) spriteAtlasTexture);
            access.setBilinear(bilinear);
            access.setMipmap(mipmap);
            SpriteAtlasTextureAccessor spriteAtlasTextureAccessor = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
            Map<Identifier, Sprite> out = new HashMap<>();
            sprites.forEach((dashIdentifier, spritePointer) -> out.put(dashIdentifier.toUndash(),loadSprite(spritePointer,loader,spriteAtlasTexture)));
            Set<Identifier> outLoad = new HashSet<>();
            List<Sprite> outAnimatedSprites = new ArrayList<>();
            animatedSprites.forEach(spritePointer -> outAnimatedSprites.add(loadSprite(spritePointer,loader,spriteAtlasTexture)));
            spriteAtlasTextureAccessor.setAnimatedSprites(outAnimatedSprites);
            spriteAtlasTextureAccessor.setSpritesToLoad(outLoad);
            spriteAtlasTextureAccessor.setSprites(out);
            spriteAtlasTextureAccessor.setId(id.toUndash());
            spriteAtlasTextureAccessor.setMaxTextureSize(maxTextureSize);
            Dash.loader.atlasData.put(spriteAtlasTexture,data);
            return spriteAtlasTexture;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Sprite loadSprite(int spritePointer,DashModelLoader loader,SpriteAtlasTexture spriteAtlasTexture) {
        Sprite sprite =  loader.registry.getSprite(spritePointer);
        ((SpriteAccessor)sprite).setAtlas(spriteAtlasTexture);
        return sprite;
    }


}
