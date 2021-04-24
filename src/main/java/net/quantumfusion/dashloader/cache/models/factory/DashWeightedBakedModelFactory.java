package net.quantumfusion.dashloader.cache.models.factory;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.DashModel;
import net.quantumfusion.dashloader.cache.models.DashWeightedBakedModel;

public class DashWeightedBakedModelFactory implements DashModelFactory {
    @Override
    public <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1 ) {
        return new DashWeightedBakedModel((WeightedBakedModel) model,registry);
    }

    @Override
    public Class<? extends BakedModel> getModelType() {
        return WeightedBakedModel.class;
    }
}
