package net.quantumfusion.dash.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.models.*;
import net.quantumfusion.dash.mixin.WeightedBakedModelEntryAccessor;
import net.quantumfusion.dash.mixin.WeightedPickerEntryAccessor;

public class DashWeightedModelEntry {
    @Serialize(order = 0)
    public Integer model;

    @Serialize(order = 1)
    public int weight;

    public DashWeightedModelEntry(@Deserialize("model") Integer model,
                                  @Deserialize("weight")int weight) {
        this.model = model;
        this.weight = weight;
    }

    public DashWeightedModelEntry(WeightedBakedModel.Entry entry, DashCache loader) {
        model = loader.registry.createModelPointer(((WeightedBakedModelEntryAccessor) entry).getModel());
        weight = ((WeightedPickerEntryAccessor) entry).getWeight();
    }

    public WeightedBakedModel.Entry toUndash(DashCache loader) {
        return new WeightedBakedModel.Entry(loader.registry.getModel(model), weight);
    }


}
