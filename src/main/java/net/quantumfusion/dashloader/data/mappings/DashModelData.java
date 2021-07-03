package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.data.serialization.Pointer2PointerMap;
import net.quantumfusion.dashloader.util.ThreadHelper;

import java.util.HashMap;
import java.util.Map;

public class DashModelData implements Dashable {


    @Serialize(order = 0)
    public final Pointer2PointerMap models;


    public DashModelData(@Deserialize("models") Pointer2PointerMap models) {
        this.models = models;
    }

    public DashModelData(VanillaData data, DashRegistry registry, DashLoader.TaskHandler taskHandler) {
        final Map<Identifier, BakedModel> models = data.getModels();
        final int size = models.size();
        this.models = new Pointer2PointerMap(size);
        taskHandler.setSubtasks(size);
        ThreadHelper.execForEach(models, (identifier, bakedModel) -> {
            if (bakedModel != null) {
                this.models.put(registry.createIdentifierPointer(identifier), registry.createModelPointer(bakedModel));
            }
            taskHandler.completedSubTask();
        });
    }


    public Map<Identifier, BakedModel> toUndash(final DashRegistry registry) {
        final HashMap<Identifier, BakedModel> out = new HashMap<>();
        models.forEach((entry) -> out.put(registry.getIdentifier(entry.key), registry.getModel(entry.value)));
        return out;
    }


}
