package net.quantumfusion.dashloader.models;

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
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashModelData {


    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Long, Long> models;


    public DashModelData(@Deserialize("models") Map<Long, Long> models) {
        this.models = models;
    }

    public DashModelData(Map<Identifier, BakedModel> models, Map<MultipartBakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData, DashRegistry registry) {
        this.models = new HashMap<>();
        models.forEach((identifier, bakedModel) -> {
            if (bakedModel != null) {
                this.models.put(registry.createIdentifierPointer(identifier), registry.createModelPointer(bakedModel, multipartData.get(bakedModel)));
            }
        });
        List<String> unsupportedModels = new ArrayList<>();
        registry.apiFailed.forEach((aClass, integer) -> unsupportedModels.add(aClass.getName()));
        unsupportedModels.stream().sorted().collect(Collectors.toList()).forEach(s -> DashLoader.LOGGER.warn("Model unsupported: " + s));
        if (!registry.apiFailed.isEmpty()) {
            DashLoader.LOGGER.warn("Models failed: " + registry.apiFailed.size());
        }
    }


    public Map<Identifier, BakedModel> toUndash(final DashRegistry registry) {
        final HashMap<Identifier, BakedModel> out = new HashMap<>();
        models.forEach((identifier, bakedModel) -> out.put(registry.getIdentifier(identifier), registry.getModel(bakedModel)));
        return out;
    }


}
