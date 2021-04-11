package net.quantumfusion.dash.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashID;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.util.ThreadHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashModelData {

    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class

    })
    @SerializeSubclasses(path = {1}, value = {
            DashBasicBakedModel.class,
            DashBuiltinBakedModel.class,
            DashMultipartBakedModel.class,
            DashWeightedBakedModel.class
    })
    public Map<DashID, DashBakedModel> models;


    public DashModelData(@Deserialize("models") Map<DashID, DashBakedModel> models) {
        this.models = models;
    }


    public DashModelData(Map<Identifier, BakedModel> models, DashModelLoader loader) {
        this.models = new HashMap<>();
        ExecutorService service = Executors.newFixedThreadPool(4);
        models.forEach((identifier, bakedModel) ->  {
            DashID id = null;
            if (identifier instanceof ModelIdentifier) {
                id = new DashModelIdentifier((ModelIdentifier) identifier);
            } else if (identifier != null) {
                System.out.println("identifier");
               id = new DashIdentifier(identifier);
            } else {
                System.err.println(bakedModel.getClass().getCanonicalName() + " identifier format is not supported by Dash, ask the developer to add support.");
            }

            if (id != null && bakedModel != null) {
                if (bakedModel instanceof BasicBakedModel) {
                    this.models.put(id, new DashBasicBakedModel((BasicBakedModel) bakedModel, loader));
                } else if (bakedModel instanceof BuiltinBakedModel) {
                    this.models.put(id, new DashBuiltinBakedModel((BuiltinBakedModel) bakedModel, loader));
                } else if (bakedModel instanceof MultipartBakedModel) {
                    this.models.put(id, new DashMultipartBakedModel((MultipartBakedModel) bakedModel, loader));
                } else if (bakedModel instanceof WeightedBakedModel) {
                    this.models.put(id, new DashWeightedBakedModel((WeightedBakedModel) bakedModel, loader));
                } else {
                    System.err.println(bakedModel.getClass().getCanonicalName() + " model format is not supported by Dash, ask the developer to add support.");
                }
            }
        });

        System.out.println("stopping");
        ThreadHelper.awaitTerminationAfterShutdown(service);
        System.out.println("stopped");
    }



    public Pair<Map<DashID, DashBakedModel>,Map<DashID, DashBakedModel>> toUndash() {
        Map<DashID, DashBakedModel> simpleModels = new HashMap<>();
        Map<DashID, DashBakedModel> advancedModels = new HashMap<>();
        models.forEach((dashModelIdentifier, dashBakedModel) -> {
            if (dashBakedModel instanceof DashBasicBakedModel || dashBakedModel instanceof DashBuiltinBakedModel) {
                simpleModels.put(dashModelIdentifier, dashBakedModel);
            } else if (dashBakedModel instanceof DashMultipartBakedModel || dashBakedModel instanceof DashWeightedBakedModel) {
                advancedModels.put(dashModelIdentifier, dashBakedModel);
            }
        });
        return Pair.of(simpleModels,advancedModels);
    }



}
