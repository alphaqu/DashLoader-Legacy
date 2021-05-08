package net.quantumfusion.dashloader.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

public interface DashModel extends Dashable {
    BakedModel toUndash(DashRegistry registry);

    default void apply(DashRegistry registry) {
    }

    int getStage();

}
