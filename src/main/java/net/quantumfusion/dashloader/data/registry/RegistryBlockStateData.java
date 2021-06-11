package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.quantumfusion.dashloader.blockstate.DashBlockState;

import java.util.Map;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, DashBlockState> blockstates;

    public RegistryBlockStateData(@Deserialize("blockstates") Map<Integer, DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }
}
