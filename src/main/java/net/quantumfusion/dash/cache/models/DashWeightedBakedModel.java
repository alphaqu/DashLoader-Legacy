package net.quantumfusion.dash.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.cache.models.components.DashWeightedModelEntry;
import net.quantumfusion.dash.mixin.WeightedBakedModelAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashWeightedBakedModel implements DashModel,DashBakedModel {

    @Serialize(order = 0)
    public List<DashWeightedModelEntry> models;

    public DashWeightedBakedModel(@Deserialize("models") List<DashWeightedModelEntry> models) {
        this.models = models;
    }

    public DashWeightedBakedModel(WeightedBakedModel model, DashModelLoader models) {
        this.models = new ArrayList<>();
        ((WeightedBakedModelAccessor) model).getModels().forEach(entry -> this.models.add(new DashWeightedModelEntry(entry, models)));
    }

    @Override
    public BakedModel toUndash(DashModelLoader loader) {
        List<WeightedBakedModel.Entry> modelsOut = new ArrayList<>();
        models.forEach(dashWeightedModelEntry -> modelsOut.add(dashWeightedModelEntry.toUndash(loader)));
        return new WeightedBakedModel(modelsOut);
    }
}
