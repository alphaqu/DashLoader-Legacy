package net.oskarstrom.dashloader.data.registry.storage.impl;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.enums.DashDataType;
import net.oskarstrom.dashloader.data.registry.storage.FactoryRegistryStorage;
import net.oskarstrom.dashloader.model.predicates.DashPredicate;
import net.oskarstrom.dashloader.model.predicates.DashStaticPredicate;

import java.util.function.Predicate;

public class PredicateFactoryRegistryStorage extends FactoryRegistryStorage<Predicate<BlockState>, DashPredicate> {

	public PredicateFactoryRegistryStorage(Class<?> originalObjectClass, DashRegistry registry, DashDataType type) {
		super(originalObjectClass, registry, type);
	}

	public int register(MultipartModelSelector selector, StateManager<Block, BlockState> stateManager) {
		final int ptr = selector.hashCode();
		if (missing(ptr)) {
			registerDashObject(ptr, obtainPredicate(selector, stateManager));
		}
		return ptr;
	}

	@Override
	public int register(Predicate<BlockState> originalObject) {
		throw new UnsupportedOperationException("Use register(MultipartModelSelector selector, StateManager<Block, BlockState> stateManager) instead.");
	}

	public DashPredicate obtainPredicate(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
		final boolean isTrue = selector == MultipartModelSelector.TRUE;
		if (selector == MultipartModelSelector.FALSE || isTrue) {
			return new DashStaticPredicate(isTrue);
		} else {
			return createFromFactory(selector, stateManager);
		}
	}

}
