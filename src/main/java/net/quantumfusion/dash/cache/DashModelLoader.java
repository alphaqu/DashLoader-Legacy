package net.quantumfusion.dash.cache;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.blockstates.DashBlockState;
import net.quantumfusion.dash.cache.models.DashBakedModel;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.BasicBakedModelAccessor;
import net.quantumfusion.dash.mixin.ModelOverideListAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashModelLoader {
    //output
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public  Map<Identifier, BakedModel> modelsOut;


    DashSpriteAtlasManager atlas;
    Object2IntMap<DashBlockState> blockstates;
    Map<DashIdentifier, DashBakedModel> models;

    public DashModelLoader(DashSpriteAtlasManager atlas, Map<DashIdentifier, DashBakedModel> models, Object2IntMap<DashBlockState> blockstates) {
        this.atlas = atlas;
        this.models = models;
        this.blockstates = blockstates;
    }

    public void load(Map<Identifier, BakedModel> modelsCachetemp) {
        //load atlas
        atlasManagerOut = atlas.toUndash();
        //load blockstates
        stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((dashBlockState, integer) -> stateLookupOut.put(dashBlockState.toUndash(), integer));
        //load models
        modelsOut = new HashMap<>();
       // models.forEach((dashIdentifier, dashBakedModel) -> modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(atlasManagerOut)));
        modelsOut.putAll(modelsCachetemp);
        //apply overrides
//        modelsOut.forEach((identifier, bakedModel) -> {
//            if (bakedModel instanceof BasicBakedModel) {
//                List<BakedModel> overridesApply = new ArrayList<>();
//                ModelOverideListAccessor itemPropertyOverrides = (ModelOverideListAccessor) ((BasicBakedModelAccessor) bakedModel).getItemPropertyOverrides();
//                itemPropertyOverrides.getOverrides().forEach(modelOverride -> overridesApply.add(modelsOut.get(modelOverride.getModelId())));
//                itemPropertyOverrides.setModels(overridesApply);
//            }
//        });

    }


}
