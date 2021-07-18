package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.data.DashID;
import net.oskarstrom.dashloader.data.DashIdentifier;
import net.oskarstrom.dashloader.data.registry.storage.AbstractRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.model.DashModelIdentifier;

public class RegistryIdentifierData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    public final Pointer2ObjectMap<DashID> identifiers;

    public RegistryIdentifierData(@Deserialize("identifiers") Pointer2ObjectMap<DashID> identifiers) {
        this.identifiers = identifiers;
    }

    public RegistryIdentifierData(AbstractRegistryStorage<Identifier, DashID> storage) {
        identifiers = storage.export();
    }

    public Int2ObjectMap<DashID> toUndash() {
        return identifiers.convert();
    }
}
