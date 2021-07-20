package net.oskarstrom.dashloader.model.predicates;

import net.minecraft.block.BlockState;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.Factory;

import java.util.function.Predicate;

public interface DashPredicate extends Factory<Predicate<BlockState>> {
	Predicate<BlockState> toUndash(DashRegistry registry);

}
