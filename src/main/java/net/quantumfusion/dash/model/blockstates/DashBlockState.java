package net.quantumfusion.dash.model.blockstates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.AbstractBlockStateAccessor;
import net.quantumfusion.dash.mixin.StateAccessor;

public class DashBlockState {
    DashIdentifier owner;
    ImmutableMap<Property<?>, Comparable<?>> entries;
    MapCodec<BlockState> codec;



    public DashBlockState(BlockState blockState) {
        StateAccessor<Block,BlockState> accessState = ((StateAccessor<Block,BlockState>) blockState);
        entries = accessState.getEntries();
        codec = accessState.getCodec();
        owner = new DashIdentifier(Registry.BLOCK.getId(accessState.getOwner()));
    }

    public BlockState toUndash() {
        return new BlockState(Registry.BLOCK.get(owner.toUndash()),entries, null);
    }

}
