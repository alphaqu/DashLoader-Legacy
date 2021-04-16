package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.DashRegistry;

public interface DashModel {
    BakedModel toUndash(DashRegistry registry);
    default void apply(DashRegistry registry){};
    DashModel toDash(BakedModel model,DashRegistry registry);
    ModelStage getStage();

}
