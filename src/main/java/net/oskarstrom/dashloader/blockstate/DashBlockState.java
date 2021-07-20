package net.oskarstrom.dashloader.blockstate;

import com.google.common.collect.ImmutableMap;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;
import net.oskarstrom.dashloader.data.serialization.Pointer2PointerMap;
import net.oskarstrom.dashloader.mixin.accessor.StateAccessor;

public class DashBlockState implements Dashable<BlockState> {

	@Serialize(order = 0)
	public final int owner;

	@Serialize(order = 1)
	public final Pointer2PointerMap entriesEncoded;


	public DashBlockState(@Deserialize("owner") int owner,
						  @Deserialize("entriesEncoded") Pointer2PointerMap entriesEncoded) {
		this.owner = owner;
		this.entriesEncoded = entriesEncoded;
	}

	public DashBlockState(BlockState blockState, DashRegistry registry) {
		StateAccessor<Block, BlockState> accessState = ((StateAccessor<Block, BlockState>) blockState);
		entriesEncoded = new Pointer2PointerMap();
		accessState.getEntries().forEach((property, comparable) -> {
			entriesEncoded.put(registry.properties.register(property), registry.propertyValues.register(comparable));
		});
		owner = registry.identifiers.register(Registry.BLOCK.getId(blockState.getBlock()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public final BlockState toUndash(final DashRegistry registry) {
		final ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
		entriesEncoded.forEach((entry) -> builder.put(registry.properties.getObject(entry.key), registry.propertyValues.getObject(entry.value)));
		return new BlockState(Registry.BLOCK.get(registry.identifiers.getObject(owner)), builder.build(), null);
	}

}
