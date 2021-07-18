package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.oskarstrom.dashloader.data.registry.storage.impl.ModelFactoryRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.model.DashModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryModelData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0, 0}, extraSubclassesId = "models")
    public final Pointer2ObjectMap<Pointer2ObjectMap<DashModel>> models;

    public RegistryModelData(@Deserialize("models") Pointer2ObjectMap<Pointer2ObjectMap<DashModel>> models) {
        this.models = models;
    }

    public RegistryModelData(ModelFactoryRegistryStorage storage) {
        Map<Integer, Pointer2ObjectMap<DashModel>> modelsToAdd = new HashMap<>();
        for (Int2ObjectMap.Entry<DashModel> entry : storage.getRegistryStorage().int2ObjectEntrySet()) {
            final DashModel value = entry.getValue();
            modelsToAdd.computeIfAbsent(value.getStage(), Pointer2ObjectMap::new).put(entry.getIntKey(), value);
        }
        models = new Pointer2ObjectMap<>(modelsToAdd);

    }

    public List<Int2ObjectMap<DashModel>> toUndash() {
        List<Int2ObjectMap<DashModel>> list = new ArrayList<>(models.size());
        models.forEach(entry -> list.add(entry.key, entry.value.convert()));
        return list;
    }
}
