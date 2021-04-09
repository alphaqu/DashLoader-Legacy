package net.quantumfusion.dash.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ModelOverrideList.class)
public interface ModelOverideListAccessor {


    @Accessor("overrides")
    List<ModelOverride> getOverrides();

    @Accessor("models")
    List<BakedModel> getModels();

    @Accessor("overrides")
    void setOverrides(List<ModelOverride> overrides);

    @Accessor("models")
    void setModels(List<BakedModel> models);
}
