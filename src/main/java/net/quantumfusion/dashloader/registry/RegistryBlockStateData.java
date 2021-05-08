package net.quantumfusion.dashloader.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.quantumfusion.dashloader.blockstates.DashBlockState;

import java.util.Map;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Long, DashBlockState> blockstates;

    public RegistryBlockStateData(@Deserialize("blockstates") Map<Long, DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }
}
