package net.quantumfusion.dash.cache.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.cache.DashCache;

import java.util.ArrayList;
import java.util.List;

public class DashExtraAtlasData {
    @Serialize(order = 0)
    public List<DashSpriteAtlasTexture> extraAtlases;

    public DashExtraAtlasData(@Deserialize(value = "extraAtlases") List<DashSpriteAtlasTexture> extraAtlases) {
        this.extraAtlases = extraAtlases;
    }


    public DashExtraAtlasData() {
        extraAtlases = new ArrayList<>();
    }


    public void addAtlas(SpriteAtlasTexture atlas,DashCache loader) {
        extraAtlases.add(new DashSpriteAtlasTexture(atlas,loader.atlasData.get(atlas),loader));
    }

    public List<SpriteAtlasTexture> toUndash(DashCache loader) {
        List<SpriteAtlasTexture> out = new ArrayList<>();
        extraAtlases.forEach(dashSpriteAtlasTexture -> out.add(dashSpriteAtlasTexture.toUndash(loader)));
        return out;
    }
}
