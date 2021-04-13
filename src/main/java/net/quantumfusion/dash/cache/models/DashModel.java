package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dash.cache.DashModelLoader;

public interface DashModel {
    BakedModel toUndash(DashModelLoader loader);


}
