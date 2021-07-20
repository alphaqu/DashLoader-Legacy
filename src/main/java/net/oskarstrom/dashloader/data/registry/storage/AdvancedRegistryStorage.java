package net.oskarstrom.dashloader.data.registry.storage;

import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;

import java.util.function.BiFunction;

public class AdvancedRegistryStorage<O, D extends Dashable<O>> extends AbstractRegistryStorage<O, D> {

	private final BiFunction<O, DashRegistry, D> function;

	public AdvancedRegistryStorage(DashRegistry registry, String objectName, BiFunction<O, DashRegistry, D> function) {
		super.init(registry, objectName);
		this.function = function;
	}

	@Override
	public int register(O originalObject) {
		final int ptr = originalObject.hashCode();
		if (missing(ptr)) {
			registryStorage.put(ptr, function.apply(originalObject, registry));
		}
		return ptr;
	}
}
