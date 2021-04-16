package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dash.cache.DashCache;

public interface DashModel {
    BakedModel toUndash(DashCache loader);
    default void apply(DashCache loader){};
    DashModel toDash(BakedModel model,DashCache loader);
    ModelStage getStage();

}
