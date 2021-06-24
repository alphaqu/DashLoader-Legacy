package net.quantumfusion.dashloader.api.predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.predicates.DashAndPredicate;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;

public class AndPredicateFactory implements PredicateFactory {
    @Override
    public DashPredicate toDash(MultipartModelSelector modelSelector, DashRegistry registry, StateManager<Block, BlockState> stateManager) {
        return new DashAndPredicate((AndMultipartModelSelector) modelSelector, stateManager, registry);
    }

    @Override
    public Class<? extends MultipartModelSelector> getType() {
        return AndMultipartModelSelector.class;
    }

    @Override
    public Class<? extends DashPredicate> getDashType() {
        return DashAndPredicate.class;
    }
}
