package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.serialization.Pointer2PointerMap;

public class DashBlockStateData {

    @Serialize(order = 0)
    public Pointer2PointerMap blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Pointer2PointerMap blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(Object2IntMap<BlockState> blockstatess, DashRegistry registry) {
        this.blockstates = new Pointer2PointerMap();
        blockstatess.forEach((blockState, integer) -> this.blockstates.put(registry.createBlockStatePointer(blockState), integer));
    }

    public Object2IntMap<BlockState> toUndash(DashRegistry registry) {
        final Object2IntOpenHashMap<BlockState> stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((entry) -> stateLookupOut.put(registry.getBlockstate(entry.key), entry.value));
        return stateLookupOut;
    }

}
