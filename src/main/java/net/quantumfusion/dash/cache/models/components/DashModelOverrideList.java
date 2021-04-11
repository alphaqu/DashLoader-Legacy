package net.quantumfusion.dash.cache.models.components;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.models.DashBakedModel;
import net.quantumfusion.dash.cache.models.DashModelLoader;
import net.quantumfusion.dash.mixin.ModelOverideListAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashModelOverrideList {
    List<DashModelOverride> overrides;
    List<DashBakedModel> bakedModels;



    public DashModelOverrideList(ModelOverrideList modelOverrideList,DashModelLoader loader) {
        overrides = new ArrayList<>();
        bakedModels = new ArrayList<>();
        ModelOverideListAccessor access =  ((ModelOverideListAccessor)modelOverrideList);
        access.getModels().forEach(bakedModel -> bakedModels.add(loader.convertSimpleModel(bakedModel)));
        access.getOverrides().forEach(modelOverride -> overrides.add(new DashModelOverride(modelOverride)));
    }

    public ModelOverrideList toUndash(DashModelLoader loader) {
        try {
            ModelOverrideList out = (ModelOverrideList) Dash.getUnsafe().allocateInstance(ModelOverrideList.class);
            List<BakedModel> outModels = new ArrayList<>();
            bakedModels.forEach(dashBakedModel -> outModels.add(dashBakedModel.toUndash(loader)));
            ((ModelOverideListAccessor)out).setModels(outModels);
            List<ModelOverride> overridesOut = new ArrayList<>();
            overrides.forEach(dashModelOverride -> overridesOut.add(dashModelOverride.toUndash()));
            ((ModelOverideListAccessor)out).setOverrides(overridesOut);
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
