package net.quantumfusion.dashloader.cache.models.factory;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.DashBasicBakedModel;
import net.quantumfusion.dashloader.cache.models.DashModel;

public class DashBasicBakedModelFactory implements DashModelFactory {


    /**
     * Creates the model to be stored.
     *
     * @param model
     * @param registry The registry
     * @param var1     An extra variable
     * @param <K>      An extra variable
     * @return A serializable model.
     */
    @Override
    public <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1) {
        return new DashBasicBakedModel((BasicBakedModel) model, registry);
    }

    @Override
    public Class<? extends BakedModel> getModelType() {
        return BasicBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashModelType() {
        return DashBasicBakedModel.class;
    }

}
