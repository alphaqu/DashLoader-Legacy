package net.quantumfusion.dash.cache.atlas;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;

import java.util.ArrayList;

public class DashSpriteAtlasManager {
    public final ArrayList<DashSpriteAtlasTexture> atlases;

    public DashSpriteAtlasManager(ArrayList<DashSpriteAtlasTexture> atlases) {
        this.atlases = atlases;
    }

    public DashSpriteAtlasManager(SpriteAtlasManager spriteAtlasManager) {
        atlases = new ArrayList<>();
        ((SpriteAtlasManagerAccessor)spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.add(new DashSpriteAtlasTexture(spriteAtlasTexture)));
    }

    public SpriteAtlasManager toUndash() {
        ArrayList<SpriteAtlasTexture> out = new ArrayList<>();
        atlases.forEach(spriteAtlasTexture -> out.add(spriteAtlasTexture.toUndash()));
        return new SpriteAtlasManager(out);
    }
}
