package net.quantumfusion.dashloader.cache.blockstates;

import com.google.common.collect.ImmutableMap;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.*;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.blockstates.properties.*;
import net.quantumfusion.dashloader.cache.models.predicates.PredicateHelper;
import net.quantumfusion.dashloader.mixin.StateAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashBlockState {

    @Serialize(order = 0)
    public final Integer owner;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeSubclasses(path = {0}, value = {
            DashBooleanProperty.class,
            DashDirectionProperty.class,
            DashEnumProperty.class,
            DashIntProperty.class
    })
    public final List<DashProperty> entriesEncoded;


    public DashBlockState(@Deserialize("owner") Integer owner,
                          @Deserialize("entriesEncoded") List<DashProperty> entriesEncoded) {
        this.owner = owner;
        this.entriesEncoded = entriesEncoded;
    }

    public DashBlockState(BlockState blockState, DashRegistry registry) {
        StateAccessor<Block, BlockState> accessState = ((StateAccessor<Block, BlockState>) blockState);
        entriesEncoded = new ArrayList<>();
        accessState.getEntries().forEach((property, comparable) -> entriesEncoded.add(PredicateHelper.getProperty(property,comparable)));
        owner = registry.createIdentifierPointer(Registry.BLOCK.getId(blockState.getBlock()));
    }

    public BlockState toUndash(final DashRegistry registry) {
        ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
        entriesEncoded.forEach(property -> builder.put(property.toUndash()));
        return new BlockState(Registry.BLOCK.get(registry.getIdentifier(owner)), builder.build(), null);
    }

}
