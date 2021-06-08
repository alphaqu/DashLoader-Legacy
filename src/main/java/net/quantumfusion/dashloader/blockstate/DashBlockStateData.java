package net.quantumfusion.dashloader.blockstate;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Pntr2PntrMap;

public class DashBlockStateData {

    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Pntr2PntrMap blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Pntr2PntrMap blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(Object2IntMap<BlockState> blockStates, DashRegistry registry) {
        this.blockstates = new Pntr2PntrMap();
        blockStates.forEach((blockState, hash) -> this.blockstates.put(registry.createBlockStatePointer(blockState), hash));
    }

    public Object2IntMap<BlockState> toUndash(DashRegistry registry) {
        final Object2IntOpenHashMap<BlockState> stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((entry) -> stateLookupOut.put(registry.getBlockState(entry.key()), entry.value()));
        return stateLookupOut;
    }

}
