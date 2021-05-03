package net.quantumfusion.dashloader.cache.blockstates;

import com.google.common.collect.ImmutableMap;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.StateAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class DashBlockState {

    @Serialize(order = 0)
    public final Long owner;

    @Serialize(order = 1)
    @SerializeNullable()
    public final HashMap<Long, Long> entriesEncoded;


    public DashBlockState(@Deserialize("owner") Long owner,
                          @Deserialize("entriesEncoded") HashMap<Long, Long> entriesEncoded) {
        this.owner = owner;
        this.entriesEncoded = entriesEncoded;
    }

    public DashBlockState(BlockState blockState, DashRegistry registry) {
        StateAccessor<Block, BlockState> accessState = ((StateAccessor<Block, BlockState>) blockState);
        entriesEncoded = new HashMap<>();
        accessState.getEntries().forEach((property, comparable) -> {
            final Pair<Long, Long> propertyPointer = registry.createPropertyPointer(property, comparable);
            entriesEncoded.put(propertyPointer.getLeft(), propertyPointer.getRight());
        });
        owner = registry.createIdentifierPointer(Registry.BLOCK.getId(blockState.getBlock()));
    }

    @SuppressWarnings("unchecked")
    public BlockState toUndash(final DashRegistry registry) {
        //loong boi
        ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
        entriesEncoded.forEach((propPntr, valuePntr) -> builder.put(registry.getProperty(propPntr, valuePntr)));
        return new BlockState(Registry.BLOCK.get(registry.getIdentifier(owner)), builder.build(), null);
    }

}
