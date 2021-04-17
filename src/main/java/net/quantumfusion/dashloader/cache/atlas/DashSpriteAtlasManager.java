package net.quantumfusion.dashloader.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.SpriteAtlasManagerAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashSpriteAtlasManager {
    @Serialize(order = 0)
    public final List<DashSpriteAtlasTexture> atlases;

    public DashSpriteAtlasManager(@Deserialize("atlases") List<DashSpriteAtlasTexture> atlases) {
        this.atlases = atlases;
    }

    public DashSpriteAtlasManager(SpriteAtlasManager spriteAtlasManager, HashMap<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData, DashRegistry loader) {
        atlases = new ArrayList<>();
        ((SpriteAtlasManagerAccessor) spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.add(new DashSpriteAtlasTexture(spriteAtlasTexture, atlasData.get(spriteAtlasTexture),loader)));

    }

    public SpriteAtlasManager toUndash(DashRegistry loader) {
        ArrayList<SpriteAtlasTexture> out = new ArrayList<>();
        atlases.forEach(spriteAtlasTexture -> out.add(spriteAtlasTexture.toUndash(loader)));
        return new SpriteAtlasManager(out);
    }
}
