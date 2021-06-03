package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.model.DashModel;

import java.util.List;
import java.util.Map;

public class RegistryModelData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0, 0})
    @SerializeSubclasses(path = {0, 1}, extraSubclassesId = "models")
    public List<Map<Long, DashModel>> models;

    public RegistryModelData(@Deserialize("models") List<Map<Long, DashModel>> models) {
        this.models = models;
    }
}
