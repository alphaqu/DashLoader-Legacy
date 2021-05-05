package net.quantumfusion.dashloader.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.WeightedBakedModelEntryAccessor;
import net.quantumfusion.dashloader.mixin.WeightedPickerEntryAccessor;

public class DashWeightedModelEntry {
    @Serialize(order = 0)
    public Long model;

    @Serialize(order = 1)
    public int weight;

    public DashWeightedModelEntry(@Deserialize("model") Long model,
                                  @Deserialize("weight") int weight) {
        this.model = model;
        this.weight = weight;
    }

    public DashWeightedModelEntry(WeightedBakedModel.Entry entry, DashRegistry registry) {
        final BakedModel model = ((WeightedBakedModelEntryAccessor) entry).getModel();
        this.model = registry.createModelPointer(model, DashLoader.getInstance().multipartData.get(model));
        weight = ((WeightedPickerEntryAccessor) entry).getWeight();
    }

    public WeightedBakedModel.Entry toUndash(DashRegistry registry) {
        return new WeightedBakedModel.Entry(registry.getModel(model), weight);
    }


}
