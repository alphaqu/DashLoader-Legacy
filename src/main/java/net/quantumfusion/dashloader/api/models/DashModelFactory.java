package net.quantumfusion.dashloader.api.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.models.DashModel;

public interface DashModelFactory {

    <K> DashModel toDash(BakedModel model, DashRegistry registry, K var1);

    Class<? extends BakedModel> getModelType();

    Class<? extends DashModel> getDashModelType();


}
