package net.quantumfusion.dashloader.api.model;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.DashWeightedBakedModel;

public class WeightedBakedModelFactory implements ModelFactory {
    @Override
    public <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1) {
        return new DashWeightedBakedModel((WeightedBakedModel) model, registry);
    }

    @Override
    public Class<? extends BakedModel> getType() {
        return WeightedBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashType() {
        return DashWeightedBakedModel.class;
    }

}
