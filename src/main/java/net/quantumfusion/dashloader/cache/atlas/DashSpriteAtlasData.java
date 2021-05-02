package net.quantumfusion.dashloader.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.SpriteAtlasManagerAccessor;
import net.quantumfusion.dashloader.util.Dashable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class DashSpriteAtlasData {
    @Serialize(order = 0)
    public final List<DashSpriteAtlasTexture> atlases;

    public DashSpriteAtlasData(@Deserialize("atlases") List<DashSpriteAtlasTexture> atlases) {
        this.atlases = atlases;
    }

    public DashSpriteAtlasData(SpriteAtlasManager spriteAtlasManager, HashMap<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData, DashRegistry loader) {
        atlases = new ArrayList<>();
        ((SpriteAtlasManagerAccessor) spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.add(new DashSpriteAtlasTexture(spriteAtlasTexture, atlasData.get(spriteAtlasTexture),loader)));

    }

    public SpriteAtlasManager toUndash(DashRegistry loader) {
        ArrayList<SpriteAtlasTexture> out = new ArrayList<>(atlases.size());
        atlases.forEach(spriteAtlasTexture -> out.add(spriteAtlasTexture.toUndash(loader)));
        return new SpriteAtlasManager(out);
    }
}
