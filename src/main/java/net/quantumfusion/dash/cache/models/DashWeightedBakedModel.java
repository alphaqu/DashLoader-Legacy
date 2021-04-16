package net.quantumfusion.dash.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.models.components.DashWeightedModelEntry;
import net.quantumfusion.dash.mixin.WeightedBakedModelAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashWeightedBakedModel implements DashModel, DashBakedModel {

    @Serialize(order = 0)
    public List<DashWeightedModelEntry> models;

    WeightedBakedModel toApply;

    public DashWeightedBakedModel() {
    }

    public DashWeightedBakedModel(@Deserialize("models") List<DashWeightedModelEntry> models) {
        this.models = models;
    }

    public DashWeightedBakedModel(WeightedBakedModel model, DashCache models) {
        this.models = new ArrayList<>();
        ((WeightedBakedModelAccessor) model).getModels().forEach(entry -> this.models.add(new DashWeightedModelEntry(entry, models)));
    }

    @Override
    public BakedModel toUndash(DashCache loader) {
        List<WeightedBakedModel.Entry> modelsOut = new ArrayList<>();
        models.forEach(dashWeightedModelEntry -> modelsOut.add((dashWeightedModelEntry.toUndash(loader))));
        new WeightedBakedModel(modelsOut);
        return new WeightedBakedModel(modelsOut);
    }

    @Override
    public DashModel toDash(BakedModel model, DashCache loader) {
        return new DashWeightedBakedModel((WeightedBakedModel) model, loader);
    }

    @Override
    public ModelStage getStage() {
        return ModelStage.INTERMEDIATE;
    }
}
