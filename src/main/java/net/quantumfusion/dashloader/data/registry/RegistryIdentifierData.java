package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.data.DashID;
import net.quantumfusion.dashloader.data.DashIdentifier;
import net.quantumfusion.dashloader.model.DashModelIdentifier;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;

public class RegistryIdentifierData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    public Pntr2ObjectMap<DashID> identifiers;

    public RegistryIdentifierData(@Deserialize("identifiers") Pntr2ObjectMap<DashID> identifiers) {
        this.identifiers = identifiers;
    }

    public RegistryIdentifierData(Int2ObjectMap<DashID> identifiers) {
        this.identifiers = new Pntr2ObjectMap<>(identifiers);
    }

    public Int2ObjectMap<DashID> toUndash() {
        return identifiers.convert();
    }
}
