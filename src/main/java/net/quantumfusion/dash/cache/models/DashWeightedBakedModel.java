package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dash.cache.models.components.DashWeightedModelEntry;
import net.quantumfusion.dash.mixin.WeightedBakedModelAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashWeightedBakedModel implements DashBakedModel{

    List<DashWeightedModelEntry> models;

    public DashWeightedBakedModel(List<DashWeightedModelEntry> models) {
        this.models = models;
    }

    public DashWeightedBakedModel(WeightedBakedModel model, DashModelLoader models) {
        this.models = new ArrayList<>();
        for (WeightedBakedModel.Entry o : ((WeightedBakedModelAccessor) model).getModels()) {
            this.models.add(new DashWeightedModelEntry(o, models));
        }
    }

    @Override
    public BakedModel toUndash(DashModelLoader loader) {
        List<WeightedBakedModel.Entry> modelsOut = new ArrayList<>();
        models.forEach(dashWeightedModelEntry -> modelsOut.add(dashWeightedModelEntry.toUndash(loader)));
        return new WeightedBakedModel(modelsOut);
    }
}
