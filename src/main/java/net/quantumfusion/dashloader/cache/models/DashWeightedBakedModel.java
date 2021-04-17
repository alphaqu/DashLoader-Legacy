package net.quantumfusion.dashloader.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.components.DashWeightedModelEntry;
import net.quantumfusion.dashloader.mixin.WeightedBakedModelAccessor;

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

    public DashWeightedBakedModel(WeightedBakedModel model, DashRegistry registry) {
        this.models = new ArrayList<>();
        ((WeightedBakedModelAccessor) model).getModels().forEach(entry -> this.models.add(new DashWeightedModelEntry(entry, registry)));
    }

    @Override
    public BakedModel toUndash(DashRegistry registry) {
        List<WeightedBakedModel.Entry> modelsOut = new ArrayList<>();
        models.forEach(dashWeightedModelEntry -> modelsOut.add((dashWeightedModelEntry.toUndash(registry))));
        new WeightedBakedModel(modelsOut);
        return new WeightedBakedModel(modelsOut);
    }

    @Override
    public DashModel toDash(BakedModel model, DashRegistry registry) {
        return new DashWeightedBakedModel((WeightedBakedModel) model, registry);
    }

    @Override
    public ModelStage getStage() {
        return ModelStage.INTERMEDIATE;
    }
}
