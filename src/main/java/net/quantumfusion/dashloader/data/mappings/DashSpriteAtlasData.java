package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.data.serialization.Object2PointerMap;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTexture;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasManagerAccessor;
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

    public DashSpriteAtlasData(VanillaData data, DashRegistry registry, DashLoader.TaskHandler taskHandler) {
        atlases = new Object2PointerMap<>();

        final Map<Identifier, SpriteAtlasTexture> atlases = ((SpriteAtlasManagerAccessor) data.getAtlasManager()).getAtlases();
        final List<SpriteAtlasTexture> extraAtlases = data.getExtraAtlases();
        taskHandler.setSubtasks(atlases.size() + extraAtlases.size());
        atlases.forEach((identifier, spriteAtlasTexture) -> {
            this.atlases.put(new DashSpriteAtlasTexture(spriteAtlasTexture, data.getAtlasData(spriteAtlasTexture), registry), 0);
            taskHandler.completedSubTask();
        });
        extraAtlases.forEach(spriteAtlasTexture -> {
            this.atlases.put(new DashSpriteAtlasTexture(spriteAtlasTexture, data.getAtlasData(spriteAtlasTexture), registry), 1);
            taskHandler.completedSubTask();
        });
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
