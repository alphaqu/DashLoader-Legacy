package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.data.serialization.Pointer2PointerMap;

public class DashBlockStateData implements Dashable {

    @Serialize(order = 0)
    public Pointer2PointerMap blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Pointer2PointerMap blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(VanillaData data, DashRegistry registry, final DashLoader.TaskHandler taskHandler) {
        this.blockstates = new Pointer2PointerMap();
        final Object2IntMap<BlockState> stateLookup = data.getStateLookup();
        taskHandler.setSubtasks(stateLookup.size());
        stateLookup.forEach((blockState, integer) -> {
            this.blockstates.put(registry.createBlockStatePointer(blockState), integer);
            taskHandler.completedSubTask();
        });
    }

    public Object2IntMap<BlockState> toUndash(DashRegistry registry) {
        final Object2IntOpenHashMap<BlockState> stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((entry) -> stateLookupOut.put(registry.getBlockstate(entry.key), entry.value));
        return stateLookupOut;
    }

}
