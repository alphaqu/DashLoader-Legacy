package net.quantumfusion.dash.cache.blockstates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.DashCacheState;

import java.util.HashMap;
import java.util.Map;

public class DashBlockStateData {

    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, Integer> blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Map<Integer, Integer> blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(Object2IntMap<BlockState> blockstatess, DashCache loader) {
        this.blockstates = new HashMap<>();
        blockstatess.forEach((blockState, integer) -> this.blockstates.put(loader.registry.createBlockStatePointer(blockState), integer));
    }

    public Object2IntMap<BlockState> toUndash(DashCache loader) {
        Object2IntMap<BlockState> stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((dashBlockState, integer) -> stateLookupOut.put(loader.registry.getBlockstate(dashBlockState), integer));
        return stateLookupOut;
    }

}
