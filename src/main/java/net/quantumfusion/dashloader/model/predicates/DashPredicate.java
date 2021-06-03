package net.quantumfusion.dashloader.model.predicates;

import net.minecraft.block.BlockState;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;

import java.util.function.Predicate;

public interface DashPredicate extends Dashable {
    Predicate<BlockState> toUndash(DashRegistry registry);

}
