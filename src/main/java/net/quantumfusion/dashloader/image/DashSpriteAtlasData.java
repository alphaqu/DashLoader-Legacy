package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasManagerAccessor;
import net.quantumfusion.dashloader.util.PairMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashSpriteAtlasData {
    @Serialize(order = 0)
    public final PairMap<DashSpriteAtlasTexture, Integer> atlases;

    public DashSpriteAtlasData(@Deserialize("atlases") PairMap<DashSpriteAtlasTexture, Integer> atlases) {
        this.atlases = atlases;
    }

    public DashSpriteAtlasData(SpriteAtlasManager spriteAtlasManager, Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData, DashRegistry registry, List<SpriteAtlasTexture> extraAtlases) {
        atlases = new PairMap<>();
        ((SpriteAtlasManagerAccessor) spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.put(new DashSpriteAtlasTexture(spriteAtlasTexture, atlasData.get(spriteAtlasTexture), registry), 0));
        extraAtlases.forEach(spriteAtlasTexture -> atlases.put(new DashSpriteAtlasTexture(spriteAtlasTexture, atlasData.get(spriteAtlasTexture), registry), 1));
    }

    public Pair<SpriteAtlasManager, List<SpriteAtlasTexture>> toUndash(DashRegistry loader) {
        ArrayList<SpriteAtlasTexture> out = new ArrayList<>(atlases.size());
        ArrayList<SpriteAtlasTexture> toRegister = new ArrayList<>(atlases.size());
        atlases.forEach((dashSpriteAtlasTexture, integer) -> {
            if (integer == 0) {
                out.add(dashSpriteAtlasTexture.toUndash(loader));
            }
            toRegister.add(dashSpriteAtlasTexture.toUndash(loader));
        });
        return Pair.of(new SpriteAtlasManager(out), toRegister);
    }
}
