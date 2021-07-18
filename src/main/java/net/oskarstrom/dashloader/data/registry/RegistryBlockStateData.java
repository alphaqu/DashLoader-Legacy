package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BlockState;
import net.oskarstrom.dashloader.blockstate.DashBlockState;
import net.oskarstrom.dashloader.data.registry.storage.AbstractRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;

public class RegistryBlockStateData {
    @Serialize(order = 0)
    public final Pointer2ObjectMap<DashBlockState> blockstates;

    public RegistryBlockStateData(@Deserialize("blockstates") Pointer2ObjectMap<DashBlockState> blockstates) {
        this.blockstates = blockstates;
    }

    public RegistryBlockStateData(AbstractRegistryStorage<BlockState, DashBlockState> storage) {
        blockstates = storage.export();
    }

    public Int2ObjectMap<DashBlockState> toUndash() {
        return blockstates.convert();
    }
}
