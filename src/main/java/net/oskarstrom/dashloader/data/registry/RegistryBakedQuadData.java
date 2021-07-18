package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.model.BakedQuad;
import net.oskarstrom.dashloader.data.registry.storage.AbstractRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.model.components.DashBakedQuad;

public class RegistryBakedQuadData {
    @Serialize(order = 0)
    public final Pointer2ObjectMap<DashBakedQuad> quads;

    public RegistryBakedQuadData(@Deserialize("quads") Pointer2ObjectMap<DashBakedQuad> quads) {
        this.quads = quads;
    }

    public RegistryBakedQuadData(AbstractRegistryStorage<BakedQuad, DashBakedQuad> storage) {
        quads = storage.export();
    }

    public Int2ObjectMap<DashBakedQuad> toUndash() {
        return quads.convert();
    }
}
