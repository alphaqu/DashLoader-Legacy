package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class RegistryModelData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0, 0}, extraSubclassesId = "models")
    public List<Pntr2ObjectMap<DashModel>> models;

    public RegistryModelData(@Deserialize("models") List<Pntr2ObjectMap<DashModel>> models) {
        this.models = models;
    }

    public List<Int2ObjectMap<DashModel>> toUndash() {
        ArrayList<Int2ObjectMap<DashModel>> outList = new ArrayList<>();
        models.forEach(dashModelPntr2ObjectMap -> outList.add(dashModelPntr2ObjectMap.convert()));
        return outList;
    }
}
