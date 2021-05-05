package net.quantumfusion.dashloader.blockstates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DashBlockStateData {

    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Long, Integer> blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Map<Long, Integer> blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(Object2IntMap<BlockState> blockstatess, DashRegistry registry) {
        this.blockstates = new HashMap<>();
        blockstatess.forEach((blockState, integer) -> this.blockstates.put(registry.createBlockStatePointer(blockState), integer));
    }

    public Object2IntMap<BlockState> toUndash(DashRegistry registry) {
        ConcurrentHashMap<BlockState, Integer> stateLookupOut = new ConcurrentHashMap<>();
        blockstates.entrySet().parallelStream().forEach((entry) -> stateLookupOut.put(registry.getBlockstate(entry.getKey()), entry.getValue()));
        return new Object2IntOpenHashMap<>(stateLookupOut);
    }

}
