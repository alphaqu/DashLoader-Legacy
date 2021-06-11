package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.blockstate.DashBlockState;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    public Pntr2ObjectMap<DashBlockState> blockStates;

    public RegistryBlockStateData(@Deserialize("blockStates") Pntr2ObjectMap<DashBlockState> blockStates) {
        this.blockStates = blockStates;
    }

    public RegistryBlockStateData(Int2ObjectMap<DashBlockState> blockStates) {
        this.blockStates = new Pntr2ObjectMap<>(blockStates);
    }


    public Int2ObjectMap<DashBlockState> toUndash() {
        return blockStates.convert();
    }
}
