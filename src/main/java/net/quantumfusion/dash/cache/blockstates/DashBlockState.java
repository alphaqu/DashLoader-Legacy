package net.quantumfusion.dash.cache.blockstates;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.*;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.StateAccessor;
import net.quantumfusion.dash.cache.properties.*;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;

public class DashBlockState {
    DashIdentifier owner;
    ImmutableMap<Property<?>, Comparable<?>> entries;
    ArrayList<DashProperty> entriesEncoded;


    public DashBlockState(BlockState blockState) {
        StateAccessor<Block, BlockState> accessState = ((StateAccessor<Block, BlockState>) blockState);
        entries = accessState.getEntries();
        entriesEncoded = new ArrayList<>();
        entries.forEach((property, comparable) -> {
            if (property instanceof BooleanProperty) {
                entriesEncoded.add(new DashBooleanProperty((BooleanProperty) property, comparable.toString()));
            } else if (property instanceof DirectionProperty) {
                entriesEncoded.add(new DashDirectionProperty((DirectionProperty) property, (Direction) comparable));
            } else if (property instanceof EnumProperty) {
                entriesEncoded.add(new DashEnumProperty((EnumProperty) property, (Enum) comparable));
            } else if (property instanceof IntProperty) {
                entriesEncoded.add(new DashIntProperty((IntProperty) property, comparable.toString()));
            }
        });
        owner = new DashIdentifier(Registry.BLOCK.getId(accessState.getOwner()));
    }

    public BlockState toUndash() {
        ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
        //TODO hardcoded, this is bad
        //above comment is correct, this is horrible
        entriesEncoded.forEach(property -> {
            if (property instanceof DashBooleanProperty) {
                MutablePair<BooleanProperty, Boolean> out = ((DashBooleanProperty) property).toUndash();
                builder.put(out.left, out.right);
            } else if (property instanceof DashDirectionProperty) {
                MutablePair<DirectionProperty, Direction> out = ((DashDirectionProperty) property).toUndash();
                builder.put(out.left, out.right);
            } else if (property instanceof DashEnumProperty) {
                MutablePair<EnumProperty, Enum> out = ((DashEnumProperty) property).toUndash();
                builder.put(out.left, out.right);
            } else if (property instanceof DashIntProperty) {
                MutablePair<IntProperty, Integer> out = ((DashIntProperty) property).toUndash();
                builder.put(out.left, out.right);
            }
        });

        return new BlockState(Registry.BLOCK.get(owner.toUndash()), builder.build(), null);
    }

}
