package net.oskarstrom.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;
import net.oskarstrom.dashloader.data.VanillaData;
import net.oskarstrom.dashloader.data.serialization.Pointer2PointerMap;
import net.oskarstrom.dashloader.util.ThreadHelper;

public class DashBlockStateData implements Dashable {

    @Serialize(order = 0)
    public final Pointer2PointerMap blockstates;

    public DashBlockStateData(@Deserialize("blockstates") Pointer2PointerMap blockstates) {
        this.blockstates = blockstates;
    }

    public DashBlockStateData(VanillaData data, DashRegistry registry, final DashLoader.TaskHandler taskHandler) {
        this.blockstates = new Pointer2PointerMap();
        final Object2IntMap<BlockState> stateLookup = data.getStateLookup();
        taskHandler.setSubtasks(stateLookup.size());
        ThreadHelper.execForEach(stateLookup, (blockState, integer) -> {
            this.blockstates.put(registry.blockstates.register(blockState), integer);
            taskHandler.completedSubTask();
        });
    }

    public Object2IntMap<BlockState> toUndash(DashRegistry registry) {
        final Object2IntOpenHashMap<BlockState> stateLookupOut = new Object2IntOpenHashMap<>();
        blockstates.forEach((entry) -> stateLookupOut.put(registry.blockstates.getObject(entry.key), entry.value));
        return stateLookupOut;
    }

}
