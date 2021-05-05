package net.quantumfusion.dashloader.models.predicates;

import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.function.Predicate;

public interface DashPredicate {
    Predicate<BlockState> toUndash(DashRegistry registry);

}
