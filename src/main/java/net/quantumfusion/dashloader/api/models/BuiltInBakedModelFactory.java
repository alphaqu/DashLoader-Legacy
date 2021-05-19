package net.quantumfusion.dashloader.api.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.models.DashBuiltinBakedModel;
import net.quantumfusion.dashloader.models.DashModel;

public class BuiltInBakedModelFactory implements ModelFactory {
    @Override
    public <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1) {
        return new DashBuiltinBakedModel((BuiltinBakedModel) model, registry);
    }

    @Override
    public Class<? extends BakedModel> getType() {
        return BuiltinBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashType() {
        return DashBuiltinBakedModel.class;
    }
}
