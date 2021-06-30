package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;
import net.quantumfusion.dashloader.model.DashModel;

import java.util.ArrayList;
import java.util.List;

public class RegistryModelData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0, 0}, extraSubclassesId = "models")
    public final Pointer2ObjectMap<Pointer2ObjectMap<DashModel>> models;

    public RegistryModelData(@Deserialize("models") Pointer2ObjectMap<Pointer2ObjectMap<DashModel>> models) {
        this.models = models;
    }


    public List<Int2ObjectMap<DashModel>> toUndash() {
        List<Int2ObjectMap<DashModel>> list = new ArrayList<>(models.size());
        models.forEach(entry -> list.add(entry.key, entry.value.convert()));
        return list;
    }
}
