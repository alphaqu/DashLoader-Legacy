package net.quantumfusion.dashloader.api.predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;

public interface PredicateFactory extends Factory<MultipartModelSelector, DashPredicate, StateManager<Block, BlockState>> {
    default FactoryType getFactoryType() {
        return FactoryType.PREDICATE;
    }
}
