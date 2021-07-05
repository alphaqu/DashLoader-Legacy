package net.quantumfusion.dashloader.model;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;

public interface DashModel extends Factory<BakedModel> {
    BakedModel toUndash(DashRegistry registry);

    default void apply(DashRegistry registry) {
    }


    int getStage();

}
