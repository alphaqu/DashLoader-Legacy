package net.quantumfusion.dash.cache.models.components;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.models.DashBakedModel;
import net.quantumfusion.dash.cache.models.DashModelLoader;
import net.quantumfusion.dash.mixin.WeightedBakedModelEntryAccessor;
import net.quantumfusion.dash.mixin.WeightedPickerEntryAccessor;

import java.util.Map;

public class DashWeightedModelEntry {
    DashBakedModel model;
    int weight;



    public DashWeightedModelEntry(WeightedBakedModel.Entry entry, DashModelLoader loader) {
        WeightedBakedModelEntryAccessor access = ((WeightedBakedModelEntryAccessor) entry);
        model = loader.convertSimpleModel(((WeightedBakedModelEntryAccessor) entry).getModel());
        weight = ((WeightedPickerEntryAccessor)entry).getWeight();
    }

    public WeightedBakedModel.Entry toUndash(DashModelLoader loader) {
        return new WeightedBakedModel.Entry(model.toUndash(loader),weight);

    }



}
