package net.quantumfusion.dashloader.model.predicates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.AndMultipartModelSelectorAccessor;
import net.quantumfusion.dashloader.util.DashHelper;

import java.util.List;
import java.util.function.Predicate;

public class DashAndPredicate implements DashPredicate {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "predicates")
    public List<DashPredicate> selectors;

    public DashAndPredicate(@Deserialize("selectors") List<DashPredicate> selectors) {
        this.selectors = selectors;
    }

    public DashAndPredicate(AndMultipartModelSelector selector, StateManager<Block, BlockState> stateManager, DashRegistry registry) {
        AndMultipartModelSelectorAccessor access = ((AndMultipartModelSelectorAccessor) selector);
        selectors = DashHelper.convertList(access.getSelectors(), selector1 -> registry.obtainPredicate(selector1, stateManager));
    }

    @Override
    public Predicate<BlockState> toUndash(DashRegistry registry) {
        List<Predicate<BlockState>> list = DashHelper.convertList(selectors, predicate -> predicate.toUndash(registry));
        return (blockState) -> list.stream().allMatch((predicate) -> predicate.test(blockState));
    }
}
