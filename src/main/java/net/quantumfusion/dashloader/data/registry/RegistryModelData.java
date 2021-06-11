package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.model.DashModel;

import java.util.List;
import java.util.Map;

public class RegistryModelData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0, 1}, extraSubclassesId = "models")
    public List<Map<Integer, DashModel>> models;

    public RegistryModelData(@Deserialize("models") List<Map<Integer, DashModel>> models) {
        this.models = models;
    }
}
