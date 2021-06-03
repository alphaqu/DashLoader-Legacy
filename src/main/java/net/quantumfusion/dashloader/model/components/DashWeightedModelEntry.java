package net.quantumfusion.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.WeightedBakedModelEntryAccessor;

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

    public DashWeightedModelEntry(Weighted.Present<BakedModel> entry, DashRegistry registry) {
        this.model = registry.createModelPointer(entry.getData());
        weight = entry.getWeight().getValue();
    }

    public Weighted.Present<BakedModel> toUndash(DashRegistry registry) {
        return WeightedBakedModelEntryAccessor.init(registry.getModel(model), Weight.of(weight));
    }


}
