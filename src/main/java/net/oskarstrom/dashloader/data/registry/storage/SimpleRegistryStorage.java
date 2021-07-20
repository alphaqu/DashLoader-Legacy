package net.oskarstrom.dashloader.data.registry.storage;

import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;

import java.util.function.Function;

public class SimpleRegistryStorage<O, D extends Dashable<O>> extends AbstractRegistryStorage<O, D> {
	private final Function<O, D> function;

	public SimpleRegistryStorage(Class<O> originalObjectClass, DashRegistry registry, Function<O, D> function) {
		super.init(registry, originalObjectClass);
		this.function = function;
	}

	@Override
	public int register(O originalObject) {
		final int ptr = originalObject.hashCode();
		if (missing(ptr)) {
			registryStorage.put(ptr, function.apply(originalObject));
		}
		return ptr;
	}
}
