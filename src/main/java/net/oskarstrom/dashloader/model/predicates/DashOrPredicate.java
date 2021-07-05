package net.oskarstrom.dashloader.model.predicates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.ExtraVariables;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.mixin.accessor.OrMultipartModelSelectorAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@DashObject(OrMultipartModelSelector.class)
public class DashOrPredicate implements DashPredicate {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "predicates")
    public final List<DashPredicate> selectors;

    public DashOrPredicate(@Deserialize("selectors") List<DashPredicate> selectors) {
        this.selectors = selectors;
    }

    @DashConstructor(ConstructorMode.FULL)
    public DashOrPredicate(OrMultipartModelSelector selector, DashRegistry registry, ExtraVariables extraVariables) {
        StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>) extraVariables.getExtraVariable1();
        OrMultipartModelSelectorAccessor access = (OrMultipartModelSelectorAccessor) selector;
        selectors = new ArrayList<>();
        access.getSelectors().forEach(selector1 -> selectors.add(registry.obtainPredicate(selector1, stateManager)));
    }

    @Override
    public Predicate<BlockState> toUndash(DashRegistry registry) {
        List<Predicate<BlockState>> list = selectors.stream().map(dashPredicate -> dashPredicate.toUndash(registry)).collect(Collectors.toList());
        return (blockState) -> list.stream().anyMatch((predicate) -> predicate.test(blockState));
    }
}
