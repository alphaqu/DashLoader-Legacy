package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;

public interface DashBakedModel {
    BakedModel toUndash(SpriteAtlasManager spriteAtlasManager);
}
