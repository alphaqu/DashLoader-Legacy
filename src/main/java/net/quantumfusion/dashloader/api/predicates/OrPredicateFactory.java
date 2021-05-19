package net.quantumfusion.dashloader.api.predicates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.models.predicates.DashOrPredicate;
import net.quantumfusion.dashloader.models.predicates.DashPredicate;

public class OrPredicateFactory implements PredicateFactory {


    @Override
    public <K> DashPredicate toDash(MultipartModelSelector modelSelector, DashRegistry registry, K var1) {
        return new DashOrPredicate((OrMultipartModelSelector) modelSelector, (StateManager<Block, BlockState>) var1, registry);
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
