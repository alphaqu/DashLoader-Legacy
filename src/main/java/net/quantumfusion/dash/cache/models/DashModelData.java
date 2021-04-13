package net.quantumfusion.dash.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashID;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.DashModelLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class DashModelData {


    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {1})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "models")
    public Map<DashID, DashModel> simpleModels;

    @Serialize(order = 1)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {1})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "models")
    public Map<DashID, DashModel> advancedModels;


    public DashModelData(@Deserialize("simpleModels") Map<DashID, DashModel> simpleModels,
                         @Deserialize("advancedModels") Map<DashID, DashModel> advancedModels) {
        this.simpleModels = simpleModels;
        this.advancedModels = advancedModels;
    }

    public DashModelData(Map<Identifier, BakedModel> models, DashModelLoader loader) {
        this.simpleModels = new HashMap<>();
        this.advancedModels = new HashMap<>();
        models.forEach((identifier, bakedModel) -> {
            DashID id = null;
            if (identifier instanceof ModelIdentifier) {
                id = new DashModelIdentifier((ModelIdentifier) identifier);
            } else if (identifier != null) {
                id = new DashIdentifier(identifier);
            } else {
                System.err.println(bakedModel.getClass().getCanonicalName() + " identifier format is not supported by Dash, ask the developer to add support.");
            }
            if (id != null && bakedModel != null) {
                if (bakedModel instanceof BasicBakedModel) {
                    this.simpleModels.put(id, new DashBasicBakedModel((BasicBakedModel) bakedModel, loader));
                } else if (bakedModel instanceof BuiltinBakedModel) {
                    this.simpleModels.put(id, new DashBuiltinBakedModel((BuiltinBakedModel) bakedModel, loader));
                } else if (bakedModel instanceof MultipartBakedModel) {
                    this.advancedModels.put(id, new DashMultipartBakedModel((MultipartBakedModel) bakedModel, loader));
                } else if (bakedModel instanceof WeightedBakedModel) {
                    this.simpleModels.put(id, new DashWeightedBakedModel((WeightedBakedModel) bakedModel, loader));
                } else {
                    System.err.println(bakedModel.getClass().getCanonicalName() + " model format is not supported by Dash, ask the developer to add support.");
                }
            }
        });
    }


    public Pair<Map<DashID, DashModel>, Map<DashID, DashModel>> toUndash() {
        return Pair.of(simpleModels, advancedModels);
    }


}
