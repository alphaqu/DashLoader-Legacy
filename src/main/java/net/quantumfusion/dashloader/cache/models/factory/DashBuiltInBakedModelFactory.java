package net.quantumfusion.dashloader.cache.models.factory;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.DashBuiltinBakedModel;
import net.quantumfusion.dashloader.cache.models.DashModel;

public class DashBuiltInBakedModelFactory implements DashModelFactory{
    @Override
    public <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1 ) {
        return new DashBuiltinBakedModel((BuiltinBakedModel) model,registry);
    }

    @Override
    public Class<? extends BakedModel> getModelType() {
        return BuiltinBakedModel.class;
    }
}
