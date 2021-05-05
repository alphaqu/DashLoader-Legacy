package net.quantumfusion.dashloader.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.DashRegistry;

public interface DashModel {
    BakedModel toUndash(DashRegistry registry);

    default void apply(DashRegistry registry) {
    }

    int getStage();

}
