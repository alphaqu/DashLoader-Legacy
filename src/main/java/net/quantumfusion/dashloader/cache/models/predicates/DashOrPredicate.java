package net.quantumfusion.dashloader.cache.models.predicates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.OrMultipartModelSelectorAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashOrPredicate implements DashPredicate {
    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeSubclasses(path = {0},  extraSubclassesId = "predicates")
    public List<DashPredicate> selectors;

    public DashOrPredicate(@Deserialize("selectors") List<DashPredicate> selectors) {
        this.selectors = selectors;
    }

    public DashOrPredicate(OrMultipartModelSelector selector, StateManager<Block, BlockState> stateManager,DashRegistry registry) {
        OrMultipartModelSelectorAccessor access = ((OrMultipartModelSelectorAccessor) selector);
        selectors = new ArrayList<>();
        access.getSelectors().forEach(selector1 -> selectors.add(PredicateHelper.getPredicate(selector1,stateManager,registry)));
    }

    @Override
    public Predicate<BlockState> toUndash(DashRegistry registry) {
        List<Predicate<BlockState>> list = selectors.stream().map(dashPredicate -> dashPredicate.toUndash(registry)).collect(Collectors.toList());
        return (blockState) -> list.stream().anyMatch((predicate) -> predicate.test(blockState));
    }
}
