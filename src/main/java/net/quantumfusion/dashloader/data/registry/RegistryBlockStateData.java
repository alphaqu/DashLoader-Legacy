package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.blockstate.DashBlockState;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.Map;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    public Pointer2ObjectMap<DashBlockState> blockstates;

    public RegistryBlockStateData(@Deserialize("blockstates") Pointer2ObjectMap<DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }

    public Map<Integer, DashBlockState> toUndash() {
        return blockstates.convert();
    }
}
