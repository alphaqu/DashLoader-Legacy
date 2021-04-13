package net.quantumfusion.dash.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.cache.models.*;
import net.quantumfusion.dash.mixin.WeightedBakedModelEntryAccessor;
import net.quantumfusion.dash.mixin.WeightedPickerEntryAccessor;

public class DashWeightedModelEntry {
    @Serialize(order = 0)
    @SerializeSubclasses(extraSubclassesId = "models")
    public DashBakedModel model;

    @Serialize(order = 1)
    public int weight;

    public DashWeightedModelEntry(@Deserialize("model") DashBakedModel model,
                                  @Deserialize("weight")int weight) {
        this.model = model;
        this.weight = weight;
    }

    public DashWeightedModelEntry(WeightedBakedModel.Entry entry, DashModelLoader loader) {
        model = (DashBakedModel) loader.convertSimpleModel(((WeightedBakedModelEntryAccessor) entry).getModel());
        weight = ((WeightedPickerEntryAccessor) entry).getWeight();
    }

    public WeightedBakedModel.Entry toUndash(DashModelLoader loader) {
        DashModel model = (DashModel) this.model;
        return new WeightedBakedModel.Entry(model.toUndash(loader), weight);

    }


}
