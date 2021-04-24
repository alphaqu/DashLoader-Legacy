package net.quantumfusion.dashloader.cache.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.cache.DashRegistry;

public interface DashModel {
    BakedModel toUndash(DashRegistry registry);
    default void apply(DashRegistry registry){};
    ModelStage getStage();

}
