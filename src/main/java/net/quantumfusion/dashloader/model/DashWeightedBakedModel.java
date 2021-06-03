package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.Weighted;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.WeightedBakedModelAccessor;
import net.quantumfusion.dashloader.model.components.DashWeightedModelEntry;

import java.util.ArrayList;
import java.util.List;

public class DashWeightedBakedModel implements DashModel {

    @Serialize(order = 0)
    public List<DashWeightedModelEntry> models;

    public DashWeightedBakedModel(@Deserialize("models") List<DashWeightedModelEntry> models) {
        this.models = models;
    }

    public DashWeightedBakedModel(WeightedBakedModel model, DashRegistry registry) {
        this.models = new ArrayList<>();
        ((WeightedBakedModelAccessor) model).getModels().forEach(entry -> this.models.add(new DashWeightedModelEntry(entry, registry)));
    }

    @Override
    public BakedModel toUndash(DashRegistry registry) {
        List<Weighted.Present<BakedModel>> modelsOut = new ArrayList<>();
        models.forEach(dashWeightedModelEntry -> modelsOut.add((dashWeightedModelEntry.toUndash(registry))));
        return new WeightedBakedModel(modelsOut);
    }

    @Override
    public int getStage() {
        return 1;
    }
}
