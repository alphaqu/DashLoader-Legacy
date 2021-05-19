package net.quantumfusion.dashloader.api.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.models.DashBasicBakedModel;
import net.quantumfusion.dashloader.models.DashModel;

public class BasicBakedModelFactory implements ModelFactory {


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
    public Class<? extends BakedModel> getType() {
        return BasicBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashType() {
        return DashBasicBakedModel.class;
    }

}
