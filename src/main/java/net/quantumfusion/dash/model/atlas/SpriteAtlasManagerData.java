package net.quantumfusion.dash.model.atlas;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;

import java.util.HashMap;
import java.util.Map;

public class SpriteAtlasManagerData {
    public final Map<DashIdentifier, DashSpriteAtlasTexture> atlases;

    public SpriteAtlasManagerData(Map<DashIdentifier, DashSpriteAtlasTexture> atlases) {
        this.atlases = atlases;
    }

    public SpriteAtlasManagerData(SpriteAtlasManager spriteAtlasManager) {
        Map<DashIdentifier, DashSpriteAtlasTexture> atlasesOut = new HashMap<>();
        ((SpriteAtlasManagerAccessor)spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlasesOut.put(new DashIdentifier(identifier),new DashSpriteAtlasTexture(spriteAtlasTexture)));

    }
}
