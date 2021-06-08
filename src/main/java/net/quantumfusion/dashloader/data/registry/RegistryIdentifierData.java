package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.data.DashID;
import net.quantumfusion.dashloader.data.DashIdentifier;
import net.quantumfusion.dashloader.model.DashModelIdentifier;

import java.util.Map;

public class RegistryIdentifierData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {1}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {0})
    public Int2ObjectSortedMap<DashID> identifiers;

    public RegistryIdentifierData(@Deserialize("identifiers") Int2ObjectSortedMap<DashID> identifiers) {
        this.identifiers = identifiers;
    }
}
