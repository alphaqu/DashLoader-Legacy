package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Pntr2PntrMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashModelData {


    @Serialize(order = 0)
    public Pntr2PntrMap models;


    public DashModelData(@Deserialize("models") Pntr2PntrMap models) {
        this.models = models;
    }

    public DashModelData(Map<Identifier, BakedModel> models, Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData, DashRegistry registry) {
        this.models = new Pntr2PntrMap(models.size());
        models.forEach((identifier, bakedModel) -> {
            if (bakedModel != null) {
                this.models.put(registry.createIdentifierPointer(identifier), registry.createModelPointer(bakedModel));
            }
        });
    }


    public Map<Identifier, BakedModel> toUndash(final DashRegistry registry) {
        final HashMap<Identifier, BakedModel> out = new HashMap<>();
        models.forEach((entry) -> out.put(registry.getIdentifier(entry.key()), registry.getModel(entry.value())));
        return out;
    }


}
