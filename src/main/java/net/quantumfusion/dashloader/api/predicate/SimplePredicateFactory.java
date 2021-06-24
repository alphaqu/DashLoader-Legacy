package net.quantumfusion.dashloader.api.predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;
import net.quantumfusion.dashloader.model.predicates.DashSimplePredicate;

public class SimplePredicateFactory implements PredicateFactory {

    @Override
    public DashPredicate toDash(MultipartModelSelector modelSelector, DashRegistry registry, StateManager<Block, BlockState> stateManager) {
        return new DashSimplePredicate((SimpleMultipartModelSelector) modelSelector, stateManager, registry);
    }

    @Override
    public Class<? extends MultipartModelSelector> getType() {
        return SimpleMultipartModelSelector.class;
    }

    @Override
    public Class<? extends DashPredicate> getDashType() {
        return DashSimplePredicate.class;
    }
}
