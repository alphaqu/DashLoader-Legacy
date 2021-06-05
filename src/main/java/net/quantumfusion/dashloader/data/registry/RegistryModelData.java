package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.model.DashModel;

import java.util.List;
import java.util.Map;

public class RegistryModelData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0, 1}, extraSubclassesId = "models")
    public List<Int2ObjectSortedMap<DashModel>> models;

    public RegistryModelData(@Deserialize("models") List<Int2ObjectSortedMap<DashModel>> models) {
        this.models = models;
    }
}
