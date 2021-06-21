package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.blockstate.DashBlockState;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    public Pointer2ObjectMap<DashBlockState> blockstates;

    public RegistryBlockStateData(@Deserialize("blockstates") Pointer2ObjectMap<DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }

    public Int2ObjectMap<DashBlockState> toUndash() {
        return blockstates.convert();
    }
}
