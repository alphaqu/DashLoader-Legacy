package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.blockstate.DashBlockState;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Int2ObjectSortedMap<DashBlockState> blockStates;

    public RegistryBlockStateData(@Deserialize("blockStates") Int2ObjectSortedMap<DashBlockState> blockStates) {
        this.blockStates = blockStates;
    }
}
