package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.WeightedBakedModelAccessor;
import net.quantumfusion.dashloader.model.components.DashWeightedModelEntry;
import net.quantumfusion.dashloader.util.DashHelper;

import java.util.List;

public class DashWeightedBakedModel implements DashModel {

    @Serialize(order = 0)
    public List<DashWeightedModelEntry> models;

    public DashWeightedBakedModel(@Deserialize("models") List<DashWeightedModelEntry> models) {
        this.models = models;
    }

    public DashWeightedBakedModel(WeightedBakedModel model, DashRegistry registry) {
        this.models = DashHelper.convertList(
                ((WeightedBakedModelAccessor) model).getModels(),
                entry -> new DashWeightedModelEntry(entry, registry));
    }

    @Override
    public BakedModel toUndash(DashRegistry registry) {
        return new WeightedBakedModel(DashHelper.convertList(models, entry -> entry.toUndash(registry)));
    }

    @Override
    public int getStage() {
        return 1;
    }
}
