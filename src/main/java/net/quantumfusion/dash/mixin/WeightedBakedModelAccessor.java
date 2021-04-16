package net.quantumfusion.dash.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WeightedBakedModel.class)
public interface WeightedBakedModelAccessor {

    @Accessor("models")
    List<WeightedBakedModel.Entry> getModels();

    @Accessor
    void setTotalWeight(int totalWeight);

    @Accessor
    void setModels(List<WeightedBakedModel.Entry> models);

    @Accessor
    void setDefaultModel(BakedModel defaultModel);
}
