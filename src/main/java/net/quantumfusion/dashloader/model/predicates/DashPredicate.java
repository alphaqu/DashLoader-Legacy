package net.quantumfusion.dashloader.model.predicates;

import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;

import java.util.function.Predicate;

public interface DashPredicate extends Factory<Predicate<BlockState>> {
   Predicate<BlockState> toUndash(DashRegistry registry);

}
