package net.quantumfusion.dashloader.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.SpriteAccessor;
import net.quantumfusion.dashloader.mixin.SpriteAtlasTextureAccessor;

import java.util.*;

public class DashSpriteAtlasTexture {
    @Serialize(order = 0)
    public List<Integer> animatedSprites;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, Integer> sprites;

    @Serialize(order = 2)
    public final Integer id;
    @Serialize(order = 3)
    public final int maxTextureSize;

    @Serialize(order = 4)
    public boolean bilinear;
    @Serialize(order = 5)
    public boolean mipmap;

    @Serialize(order = 6)
    public DashSpriteAtlasTextureData data;



    public DashSpriteAtlasTexture(@Deserialize("animatedSprites") List<Integer> animatedSprites,
                                  @Deserialize("sprites") Map<Integer, Integer> sprites,
                                  @Deserialize("id") Integer id,
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

    public DashSpriteAtlasTexture(SpriteAtlasTexture spriteAtlasTexture, DashSpriteAtlasTextureData data, DashRegistry registry) {
        SpriteAtlasTextureAccessor spriteTextureAccess = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
        this.data = data;
        animatedSprites = new ArrayList<>();
        sprites = new HashMap<>();
        spriteTextureAccess.getAnimatedSprites().forEach(sprite -> animatedSprites.add(registry.createSpritePointer(sprite)));
        spriteTextureAccess.getSprites().forEach((identifier, sprite) -> sprites.put(registry.createIdentifierPointer(identifier), registry.createSpritePointer(sprite)));
        id = registry.createIdentifierPointer(spriteAtlasTexture.getId());
        maxTextureSize = spriteTextureAccess.getMaxTextureSize();
        bilinear = ((AbstractTextureAccessor)spriteAtlasTexture).getBilinear();
        mipmap = ((AbstractTextureAccessor)spriteAtlasTexture).getMipmap();
    }

    public SpriteAtlasTexture toUndash(DashRegistry registry) {
        try {
            SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture) DashLoader.getInstance().getUnsafe().allocateInstance(SpriteAtlasTexture.class);
            AbstractTextureAccessor access = ((AbstractTextureAccessor) spriteAtlasTexture);
            access.setBilinear(bilinear);
            access.setMipmap(mipmap);
            SpriteAtlasTextureAccessor spriteAtlasTextureAccessor = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
            Map<Identifier, Sprite> out = new HashMap<>();
            sprites.forEach((dashIdentifier, spritePointer) -> out.put(registry.getIdentifier(dashIdentifier), loadSprite(spritePointer,registry,spriteAtlasTexture)));
            Set<Identifier> outLoad = new HashSet<>();
            List<Sprite> outAnimatedSprites = new ArrayList<>();
            animatedSprites.forEach(spritePointer -> outAnimatedSprites.add(loadSprite(spritePointer,registry,spriteAtlasTexture)));
            spriteAtlasTextureAccessor.setAnimatedSprites(outAnimatedSprites);
            spriteAtlasTextureAccessor.setSpritesToLoad(outLoad);
            spriteAtlasTextureAccessor.setSprites(out);
            spriteAtlasTextureAccessor.setId(registry.getIdentifier(id));
            spriteAtlasTextureAccessor.setMaxTextureSize(maxTextureSize);
            DashLoader.getInstance().atlasData.put(spriteAtlasTexture, data);
            return spriteAtlasTexture;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Sprite loadSprite(int spritePointer, DashRegistry registry, SpriteAtlasTexture spriteAtlasTexture) {
        Sprite sprite =  registry.getSprite(spritePointer);
        ((SpriteAccessor)sprite).setAtlas(spriteAtlasTexture);
        return sprite;
    }


}
