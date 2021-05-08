package net.quantumfusion.dashloader.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.common.DashID;
import net.quantumfusion.dashloader.common.DashIdentifier;
import net.quantumfusion.dashloader.models.DashModelIdentifier;

import java.util.Map;

public class RegistryIdentifierData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {1}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {0})
    public Map<Long, DashID> identifiers;

    public RegistryIdentifierData(@Deserialize("identifiers") Map<Long, DashID> identifiers) {
        this.identifiers = identifiers;
    }
}
