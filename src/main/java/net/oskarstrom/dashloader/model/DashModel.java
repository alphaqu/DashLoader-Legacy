package net.oskarstrom.dashloader.model;

import net.minecraft.client.render.model.BakedModel;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.Factory;

public interface DashModel extends Factory<BakedModel> {
    BakedModel toUndash(DashRegistry registry);

    default void apply(DashRegistry registry) {
    }


    default int getStage() {
        return 0;
    }

}
