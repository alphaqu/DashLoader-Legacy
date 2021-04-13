package net.quantumfusion.dash.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.cache.models.*;
import net.quantumfusion.dash.mixin.ModelOverideListAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashModelOverrideList {
    @Serialize(order = 0)
    public List<DashModelOverride> overrides;
    @Serialize(order = 1)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {0}, extraSubclassesId = "models")
    public List<DashModel> bakedModels;

    public DashModelOverrideList(@Deserialize("overrides")List<DashModelOverride> overrides,
                                 @Deserialize("bakedModels")List<DashModel> bakedModels) {
        this.overrides = overrides;
        this.bakedModels = bakedModels;
    }

    public DashModelOverrideList(ModelOverrideList modelOverrideList, DashModelLoader loader) {
        overrides = new ArrayList<>();
        bakedModels = new ArrayList<>();
        ModelOverideListAccessor access = ((ModelOverideListAccessor) modelOverrideList);
        List<BakedModel> models =  access.getModels();

        models.forEach(bakedModel -> bakedModels.add(loader.convertSimpleModel(bakedModel)));
        access.getOverrides().forEach(modelOverride -> overrides.add(new DashModelOverride(modelOverride)));
    }

    public ModelOverrideList toUndash(DashModelLoader loader) {
        try {
            ModelOverrideList out = (ModelOverrideList) Dash.getUnsafe().allocateInstance(ModelOverrideList.class);
            List<BakedModel> outModels = new ArrayList<>();
            bakedModels.forEach(dashBakedModel -> {
                if (dashBakedModel != null) {
                    outModels.add(dashBakedModel.toUndash(loader));
                } else {
                    outModels.add(null);
                }
            });
            ((ModelOverideListAccessor) out).setModels(outModels);
            List<ModelOverride> overridesOut = new ArrayList<>();
            overrides.forEach(dashModelOverride -> overridesOut.add(dashModelOverride.toUndash()));
            ((ModelOverideListAccessor) out).setOverrides(overridesOut);
            return out;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        try {
            throw new Exception("Unsafe failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
