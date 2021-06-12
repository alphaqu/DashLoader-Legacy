package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTexture;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasManagerAccessor;
import net.quantumfusion.dashloader.util.serialization.Object2PointerMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashSpriteAtlasData {
    @Serialize(order = 0)
    public Object2PointerMap<DashSpriteAtlasTexture> atlases;

    public DashSpriteAtlasData(@Deserialize("atlases") Object2PointerMap<DashSpriteAtlasTexture> atlases) {
        this.atlases = atlases;
    }

    public DashSpriteAtlasData(SpriteAtlasManager spriteAtlasManager, Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData, DashRegistry registry, List<SpriteAtlasTexture> extraAtlases) {
        atlases = new Object2PointerMap<>();
        ((SpriteAtlasManagerAccessor) spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.put(new DashSpriteAtlasTexture(spriteAtlasTexture, atlasData.get(spriteAtlasTexture), registry), 0));
        extraAtlases.forEach(spriteAtlasTexture -> atlases.put(new DashSpriteAtlasTexture(spriteAtlasTexture, atlasData.get(spriteAtlasTexture), registry), 1));
    }

    public Pair<SpriteAtlasManager, List<SpriteAtlasTexture>> toUndash(DashRegistry loader) {
        ArrayList<SpriteAtlasTexture> out = new ArrayList<>(atlases.size());
        ArrayList<SpriteAtlasTexture> toRegister = new ArrayList<>(atlases.size());
        atlases.forEach((entry) -> {
            final DashSpriteAtlasTexture key = entry.key;
            if (entry.value == 0) {
                out.add(key.toUndash(loader));
            }
            toRegister.add(key.toUndash(loader));
        });
        return Pair.of(new SpriteAtlasManager(out), toRegister);
    }
}
