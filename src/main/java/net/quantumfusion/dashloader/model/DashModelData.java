package net.quantumfusion.dashloader.model;

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
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.PairMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashModelData {


    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public PairMap<Long, Long> models;


    public DashModelData(@Deserialize("models") PairMap<Long, Long> models) {
        this.models = models;
    }

    public DashModelData(Map<Identifier, BakedModel> models, Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData, DashRegistry registry) {
        this.models = new PairMap<>(models.size());
        models.forEach((identifier, bakedModel) -> {
            if (bakedModel != null) {
                this.models.put(registry.createIdentifierPointer(identifier), registry.createModelPointer(bakedModel));
            }
        });
    }


    public Map<Identifier, BakedModel> toUndash(final DashRegistry registry) {
        final HashMap<Identifier, BakedModel> out = new HashMap<>();
        models.forEach((identifier, bakedModel) -> out.put(registry.getIdentifier(identifier), registry.getModel(bakedModel)));
        return out;
    }


}
