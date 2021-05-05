package net.quantumfusion.dashloader.api.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.models.DashModel;
import net.quantumfusion.dashloader.models.DashWeightedBakedModel;

public class DashWeightedBakedModelFactory implements DashModelFactory {
    @Override
    public <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1) {
        return new DashWeightedBakedModel((WeightedBakedModel) model, registry);
    }

    @Override
    public Class<? extends BakedModel> getModelType() {
        return WeightedBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashModelType() {
        return DashWeightedBakedModel.class;
    }
}