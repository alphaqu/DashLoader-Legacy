package net.quantumfusion.dashloader.api.predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.predicates.DashOrPredicate;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;

public class OrPredicateFactory implements PredicateFactory {


    @Override
    public DashPredicate toDash(MultipartModelSelector modelSelector, DashRegistry registry, StateManager<Block, BlockState> stateManager) {
        return new DashOrPredicate((OrMultipartModelSelector) modelSelector, stateManager, registry);
    }

    @Override
    public Class<? extends MultipartModelSelector> getType() {
        return OrMultipartModelSelector.class;
    }

    @Override
    public Class<? extends DashPredicate> getDashType() {
        return DashOrPredicate.class;
    }
}
