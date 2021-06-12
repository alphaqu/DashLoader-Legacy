package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.data.DashID;
import net.quantumfusion.dashloader.data.DashIdentifier;
import net.quantumfusion.dashloader.model.DashModelIdentifier;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.Map;

public class RegistryIdentifierData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    public Pointer2ObjectMap<DashID> identifiers;

    public RegistryIdentifierData(@Deserialize("identifiers") Pointer2ObjectMap<DashID> identifiers) {
        this.identifiers = identifiers;
    }


    public Map<Integer, DashID> toUndash() {
        return identifiers.convert();
    }
}
