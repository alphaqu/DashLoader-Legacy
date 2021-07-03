package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.serialization.Pointer2PointerMap;
import net.quantumfusion.dashloader.mixin.accessor.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.util.UnsafeHelper;

import java.util.*;

public class DashSpriteAtlasTexture {
    @Serialize(order = 0)
    public final int id;
    @Serialize(order = 1)
    public final int maxTextureSize;
    @Serialize(order = 2)
    public Pointer2PointerMap sprites;
    @Serialize(order = 3)
    public boolean bilinear;
    @Serialize(order = 4)
    public boolean mipmap;
    @Serialize(order = 5)
    public DashSpriteAtlasTextureData data;


    public DashSpriteAtlasTexture(@Deserialize("id") int id,
                                  @Deserialize("maxTextureSize") int maxTextureSize,
                                  @Deserialize("sprites") Pointer2PointerMap sprites,
                                  @Deserialize("bilinear") boolean bilinear,
                                  @Deserialize("mipmap") boolean mipmap,
                                  @Deserialize("data") DashSpriteAtlasTextureData data) {
        this.id = id;
        this.maxTextureSize = maxTextureSize;
        this.sprites = sprites;
        this.bilinear = bilinear;
        this.mipmap = mipmap;
        this.data = data;
    }

    public DashSpriteAtlasTexture(SpriteAtlasTexture spriteAtlasTexture, DashSpriteAtlasTextureData data, DashRegistry registry) {
        SpriteAtlasTextureAccessor spriteTextureAccess = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
        this.data = data;
        sprites = new Pointer2PointerMap();
        spriteTextureAccess.getSprites().forEach((identifier, sprite) -> sprites.put(registry.createIdentifierPointer(identifier), registry.createSpritePointer(sprite)));
        id = registry.createIdentifierPointer(spriteAtlasTexture.getId());
        maxTextureSize = spriteTextureAccess.getMaxTextureSize();
        bilinear = ((AbstractTextureAccessor) spriteAtlasTexture).getBilinear();
        mipmap = ((AbstractTextureAccessor) spriteAtlasTexture).getMipmap();
    }

    public SpriteAtlasTexture toUndash(DashRegistry registry) {
        final SpriteAtlasTexture spriteAtlasTexture = UnsafeHelper.allocateInstance(SpriteAtlasTexture.class);
        final AbstractTextureAccessor access = ((AbstractTextureAccessor) spriteAtlasTexture);
        access.setBilinear(bilinear);
        access.setMipmap(mipmap);
        final SpriteAtlasTextureAccessor spriteAtlasTextureAccessor = ((SpriteAtlasTextureAccessor) spriteAtlasTexture);
        final Map<Identifier, Sprite> out = new HashMap<>(sprites.size());
        sprites.forEach((entry) -> out.put(registry.getIdentifier(entry.key), loadSprite(entry.value, registry, spriteAtlasTexture)));
        final List<TextureTickListener> outAnimatedSprites = new ArrayList<>();
        out.values().forEach(sprite -> {
            final TextureTickListener animation = sprite.getAnimation();
            if (animation != null) {
                outAnimatedSprites.add(animation);
            }
        });
        spriteAtlasTextureAccessor.setAnimatedSprites(outAnimatedSprites);
        spriteAtlasTextureAccessor.setSpritesToLoad(new HashSet<>());
        spriteAtlasTextureAccessor.setSprites(out);
        spriteAtlasTextureAccessor.setId(registry.getIdentifier(id));
        spriteAtlasTextureAccessor.setMaxTextureSize(maxTextureSize);
        DashLoader.getVanillaData().addAtlasData(spriteAtlasTexture, data);
        return spriteAtlasTexture;
    }

    private Sprite loadSprite(int spritePointer, DashRegistry registry, SpriteAtlasTexture spriteAtlasTexture) {
        Sprite sprite = registry.getSprite(spritePointer);
        ((SpriteAccessor) sprite).setAtlas(spriteAtlasTexture);
        return sprite;
    }


}
