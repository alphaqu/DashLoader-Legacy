package net.quantumfusion.dashloader.blockstate;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
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
    public Int2IntMap blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Int2IntMap blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(Object2IntMap<BlockState> blockStates, DashRegistry registry) {
        this.blockstates = new Int2IntLinkedOpenHashMap();
        blockStates.forEach((blockState, hash) -> this.blockstates.put(registry.createBlockStatePointer(blockState), hash));
    }

    public Object2IntMap<BlockState> toUndash(DashRegistry registry) {
        final ConcurrentHashMap<BlockState, Integer> stateLookupOut = new ConcurrentHashMap<>();
        blockstates.entrySet().parallelStream().forEach((entry) -> stateLookupOut.put(registry.getBlockState(entry.getKey()), entry.getValue()));
        return new Object2IntOpenHashMap<>(stateLookupOut);
    }

}
