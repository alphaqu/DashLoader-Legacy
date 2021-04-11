package net.quantumfusion.dash.cache.models;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.blockstates.DashBlockState;
import net.quantumfusion.dash.util.ThreadHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class DashModelLoader {
    //output
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;


    DashSpriteAtlasManager atlas;
    Object2IntMap<DashBlockState> blockstates;

    Map<DashIdentifier, DashBakedModel> models;

    Map<DashIdentifier, DashBakedModel> modelsToLoad;

    public DashModelLoader(DashSpriteAtlasManager atlas, Map<DashIdentifier, DashBakedModel> models, Object2IntMap<DashBlockState> blockstates) {
        this.atlas = atlas;
        this.models = models;
        this.blockstates = blockstates;
    }

    public DashModelLoader(SpriteAtlasManager atlasManager,
                           Object2IntMap<BlockState> stateLookup,
                           Map<Identifier, BakedModel> models) {
        System.out.println("Caching atlas");


        byte[] buffer = new byte[20000000];
        BinarySerializer<DashSpriteAtlasManager> serializer = SerializerBuilder.create()
                .build(DashSpriteAtlasManager.class);
        serializer.encode(buffer,0,new DashSpriteAtlasManager(atlasManager));
        this.atlas = serializer.decode(buffer,0);
        this.blockstates = new Object2IntOpenHashMap<>();
        this.models = new HashMap<>();
        modelsOut = new HashMap<>();
        this.modelsToLoad = new HashMap<>();
        System.out.println("Caching blockstates");
        stateLookup.forEach((blockState, integer) -> blockstates.put((new DashBlockState(blockState)), integer));
        System.out.println("Caching models");
        Dash.modelLoading = Executors.newFixedThreadPool(4);
        models.forEach((identifier, bakedModel) -> Dash.modelLoading.submit(() -> {
            if (bakedModel instanceof BasicBakedModel) {
                this.models.put(new DashModelIdentifier((ModelIdentifier) identifier), new DashBasicBakedModel((BasicBakedModel) bakedModel,this));
            } else if (bakedModel instanceof BuiltinBakedModel) {
                this.models.put(new DashModelIdentifier((ModelIdentifier) identifier), new DashBuiltinBakedModel((BuiltinBakedModel) bakedModel,this));
            } else if (bakedModel instanceof MultipartBakedModel) {
                this.modelsToLoad.put(new DashModelIdentifier((ModelIdentifier) identifier), new DashMultipartBakedModel((MultipartBakedModel) bakedModel, this));
            } else if (bakedModel instanceof WeightedBakedModel) {
                this.modelsToLoad.put(new DashModelIdentifier((ModelIdentifier) identifier), new DashWeightedBakedModel((WeightedBakedModel) bakedModel, this));
            } else {
                System.err.println(bakedModel.getClass().getCanonicalName() + " is not supported by Dash, ask the developer to add support.");
            }
        }));
        ThreadHelper.awaitTerminationAfterShutdown(Dash.modelLoading);
        System.out.println("Caching complete.");
    }

    public DashBakedModel convertSimpleModel(BakedModel model) {
        if (model instanceof BasicBakedModel) {
            return new DashBasicBakedModel((BasicBakedModel) model,this);
        } else if (model instanceof BuiltinBakedModel) {
            return new DashBuiltinBakedModel((BuiltinBakedModel) model,this);
        } else if (model instanceof MultipartBakedModel) {
            return new DashMultipartBakedModel((MultipartBakedModel) model, this);
        } else if (model instanceof WeightedBakedModel) {
            return new DashWeightedBakedModel((WeightedBakedModel) model, this);
        } else {
            System.err.println(model.getClass().getCanonicalName() + " is not supported by Dash, ask the developer to add support.");
        }
        return null;
    }


    public void load() {

        //load atlas
        System.out.println("Loading atlas");
        atlasManagerOut = atlas.toUndash();
        //load blockstates
        System.out.println("Loading blockstates");
        stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((dashBlockState, integer) -> stateLookupOut.put(dashBlockState.toUndash(), integer));
        System.out.println("Loading simple models");
        //load basicmodels
        models.forEach((dashIdentifier, dashBakedModel) -> modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(this)));
        System.out.println("Loading complex models");
        //load other models
        modelsToLoad.forEach((dashIdentifier, dashBakedModel) -> modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(this)));

    }


}
