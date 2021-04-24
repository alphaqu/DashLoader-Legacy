package net.quantumfusion.dashloader.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.DashRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashModelData {


    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Integer, Integer> models;


    public DashModelData(@Deserialize("models") Map<Integer, Integer> models) {
        this.models = models;
    }

    public DashModelData(Map<Identifier, BakedModel> models, HashMap<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData, DashRegistry registry) {
        this.models = new HashMap<>();
        models.forEach((identifier, bakedModel) -> {
            if (bakedModel != null) {
                this.models.put(registry.createIdentifierPointer(identifier), registry.createModelPointer(bakedModel,multipartData.get(bakedModel)));
            }
        });
    }


    public Map<Identifier, BakedModel> toUndash(DashRegistry registry) {
        Map<Identifier, BakedModel> out = new HashMap<>();
        models.forEach((integer, integer2) -> out.put(registry.getIdentifier(integer), registry.getModel(integer2)));
        return out;
    }


}
