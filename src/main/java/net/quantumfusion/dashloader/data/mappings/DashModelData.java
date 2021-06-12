package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.serialization.Pointer2PointerMap;

import java.util.HashMap;
import java.util.Map;

public class DashModelData {


    @Serialize(order = 0)
    public Pointer2PointerMap models;


    public DashModelData(@Deserialize("models") Pointer2PointerMap models) {
        this.models = models;
    }

    public DashModelData(Map<Identifier, BakedModel> models, DashRegistry registry) {
        this.models = new Pointer2PointerMap(models.size());
        models.forEach((identifier, bakedModel) -> {
            if (bakedModel != null) {
                this.models.put(registry.createIdentifierPointer(identifier), registry.createModelPointer(bakedModel));
            }
        });
    }


    public Map<Identifier, BakedModel> toUndash(final DashRegistry registry) {
        final HashMap<Identifier, BakedModel> out = new HashMap<>();
        models.forEach((entry) -> out.put(registry.getIdentifier(entry.key), registry.getModel(entry.value)));
        return out;
    }


}
