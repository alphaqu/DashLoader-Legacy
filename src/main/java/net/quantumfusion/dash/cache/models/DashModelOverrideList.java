package net.quantumfusion.dash.cache.models;

import com.google.common.collect.Lists;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.mixin.ModelOverideListAccessor;
import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;

public class DashModelOverrideList {
    List<DashModelOverride> overrides;
    int models;



    public DashModelOverrideList(ModelOverrideList modelOverrideList) {
        overrides = new ArrayList<>();
        ModelOverideListAccessor access =  ((ModelOverideListAccessor)modelOverrideList);
        models = 0;
        access.getModels().forEach(bakedModel -> models++);
        access.getOverrides().forEach(modelOverride -> overrides.add(new DashModelOverride(modelOverride)));
    }

    public ModelOverrideList toUndash() {
        try {
            ModelOverrideList out = (ModelOverrideList) Dash.getUnsafe().allocateInstance(ModelOverrideList.class);
            List<BakedModel> outModels = new ArrayList<>();
            for (int i = 0; i < models; i++) {
                outModels.add(null);
            }
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
