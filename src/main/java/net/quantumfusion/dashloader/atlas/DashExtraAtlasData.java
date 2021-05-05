package net.quantumfusion.dashloader.atlas;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;

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


    public void addAtlas(SpriteAtlasTexture atlas, DashRegistry registry) {
        extraAtlases.add(new DashSpriteAtlasTexture(atlas, DashLoader.getInstance().atlasData.get(atlas), registry));
    }


    public List<SpriteAtlasTexture> toUndash(DashRegistry registry) {
        List<SpriteAtlasTexture> out = new ArrayList<>();
        extraAtlases.forEach(dashSpriteAtlasTexture -> out.add(dashSpriteAtlasTexture.toUndash(registry)));
        return out;
    }
}
