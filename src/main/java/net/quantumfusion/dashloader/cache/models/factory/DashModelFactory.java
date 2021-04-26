package net.quantumfusion.dashloader.cache.models.factory;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.DashModel;
import org.apache.commons.lang3.tuple.Pair;

public interface DashModelFactory {

    <K>  DashModel toDash(BakedModel model, DashRegistry registry, K var1);
    Class<? extends BakedModel> getModelType();




}
