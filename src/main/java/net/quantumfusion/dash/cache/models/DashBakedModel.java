package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedModel;

public interface DashBakedModel {
    BakedModel toUndash(DashModelLoader loader);
}
