package net.quantumfusion.dashloader.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.ModelOverideListAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashModelOverrideList {
    @Serialize(order = 0)
    public List<DashModelOverride> overrides;
    @Serialize(order = 1)
    @SerializeNullable(path = {0})
    public List<Integer> bakedModels;

    ModelOverrideList toApply;

    public DashModelOverrideList(@Deserialize("overrides")List<DashModelOverride> overrides,
                                 @Deserialize("bakedModels")List<Integer> bakedModels) {
        this.overrides = overrides;
        this.bakedModels = bakedModels;
    }

    public DashModelOverrideList(ModelOverrideList modelOverrideList, DashRegistry registry) {
        overrides = new ArrayList<>();
        bakedModels = new ArrayList<>();
        ModelOverideListAccessor access = ((ModelOverideListAccessor) modelOverrideList);
        List<BakedModel> models =  access.getModels();

        models.forEach(bakedModel -> bakedModels.add(registry.createModelPointer(bakedModel, DashLoader.getInstance().multipartData.get(bakedModel))));
        access.getOverrides().forEach(modelOverride -> overrides.add(new DashModelOverride(modelOverride,registry)));
    }

    public ModelOverrideList toUndash(DashRegistry registry) {
        ModelOverrideList out = Unsafe.allocateInstance(ModelOverrideList.class);

        List<ModelOverride> overridesOut = new ArrayList<>();
        overrides.forEach(dashModelOverride -> overridesOut.add(dashModelOverride.toUndash(registry)));
        ((ModelOverideListAccessor) out).setOverrides(overridesOut);
        toApply = out;
        return out;
    }

    public void applyOverrides(DashRegistry registry) {
        List<BakedModel> outModels = new ArrayList<>();
        bakedModels.forEach(dashBakedModel -> {
            if (dashBakedModel != null) {
                outModels.add(registry.getModel(dashBakedModel));
            } else {
                outModels.add(null);
            }
        });
        ((ModelOverideListAccessor) toApply).setModels(outModels);
    }
}
