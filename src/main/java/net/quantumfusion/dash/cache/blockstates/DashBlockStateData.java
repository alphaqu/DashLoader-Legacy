package net.quantumfusion.dash.cache.blockstates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class DashBlockStateData {

    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<DashBlockState, Integer> blockstates;

    public Object2IntMap<BlockState> stateLookupOut;

    public DashBlockStateData(@Deserialize("blockstates") Map<DashBlockState, Integer> blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(Object2IntMap<BlockState> blockstatess) {
        this.blockstates = new HashMap<>();
        blockstatess.forEach((blockState, integer) -> this.blockstates.put(new DashBlockState(blockState), integer));
    }

    public void toUndash() {
        stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((dashBlockState, integer) -> dashBlockState.toUndash());
    }

    public Object2IntMap<BlockState> load() {
        stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((dashBlockState, integer) -> stateLookupOut.put(dashBlockState.toUndash(), integer));
        return stateLookupOut;
    }

}
