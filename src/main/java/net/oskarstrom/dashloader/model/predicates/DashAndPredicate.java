package net.oskarstrom.dashloader.model.predicates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.ExtraVariables;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.mixin.accessor.AndMultipartModelSelectorAccessor;
import net.oskarstrom.dashloader.util.DashHelper;

import java.util.List;
import java.util.function.Predicate;

@DashObject(AndMultipartModelSelector.class)
public class DashAndPredicate implements DashPredicate {
	@Serialize(order = 0)
	@SerializeSubclasses(path = {0}, extraSubclassesId = "predicates")
	public final List<DashPredicate> selectors;

	public DashAndPredicate(@Deserialize("selectors") List<DashPredicate> selectors) {
		this.selectors = selectors;
	}

	public DashAndPredicate(AndMultipartModelSelector selector, DashRegistry registry, ExtraVariables extraVariables) {
		StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>) extraVariables.getExtraVariable1();
		AndMultipartModelSelectorAccessor access = ((AndMultipartModelSelectorAccessor) selector);
		selectors = DashHelper.convertList(access.getSelectors(), selector1 -> registry.predicates.obtainPredicate(selector1, stateManager));
	}

	@Override
	public Predicate<BlockState> toUndash(DashRegistry registry) {
		List<Predicate<BlockState>> list = DashHelper.convertList(selectors, predicate -> predicate.toUndash(registry));
		return (blockState) -> list.stream().allMatch((predicate) -> predicate.test(blockState));
	}
}
