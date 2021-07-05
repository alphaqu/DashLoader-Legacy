package net.oskarstrom.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.mixin.accessor.WeightedBakedModelAccessor;
import net.oskarstrom.dashloader.model.components.DashWeightedModelEntry;
import net.oskarstrom.dashloader.util.DashHelper;

import java.util.List;

@DashObject(WeightedBakedModel.class)
public class DashWeightedBakedModel implements DashModel {

    @Serialize(order = 0)
    public final List<DashWeightedModelEntry> models;

    public DashWeightedBakedModel(@Deserialize("models") List<DashWeightedModelEntry> models) {
        this.models = models;
    }

    public DashWeightedBakedModel(WeightedBakedModel model, DashRegistry registry) {
        this.models = DashHelper.convertList(
                ((WeightedBakedModelAccessor) model).getModels(),
                entry -> new DashWeightedModelEntry(entry, registry));
    }

    @Override
    public WeightedBakedModel toUndash(DashRegistry registry) {
        return new WeightedBakedModel(DashHelper.convertList(models, entry -> entry.toUndash(registry)));
    }

    @Override
    public int getStage() {
        return 1;
    }
}
