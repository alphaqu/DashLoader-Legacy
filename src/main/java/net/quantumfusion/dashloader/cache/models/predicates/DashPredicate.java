package net.quantumfusion.dashloader.cache.models.predicates;

import net.minecraft.block.BlockState;

import java.util.function.Predicate;

public interface DashPredicate {
    Predicate<BlockState> toUndash();

}
