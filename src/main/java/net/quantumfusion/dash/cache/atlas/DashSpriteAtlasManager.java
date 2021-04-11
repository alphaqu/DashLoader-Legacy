package net.quantumfusion.dash.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashSpriteAtlasManager {
    @Serialize(order = 0)
    public final List<DashSpriteAtlasTexture> atlases;

    public DashSpriteAtlasManager(@Deserialize("atlases") List<DashSpriteAtlasTexture> atlases) {
        this.atlases = atlases;
    }

    public DashSpriteAtlasManager(SpriteAtlasManager spriteAtlasManager) {
        atlases = new ArrayList<>();
        ((SpriteAtlasManagerAccessor) spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.add(new DashSpriteAtlasTexture(spriteAtlasTexture)));
    }

    public SpriteAtlasManager toUndash() {
        ArrayList<SpriteAtlasTexture> out = new ArrayList<>();
        atlases.forEach(spriteAtlasTexture -> out.add(spriteAtlasTexture.toUndash()));
        return new SpriteAtlasManager(out);
    }
}
