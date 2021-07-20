package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.DashRegistry;

public interface DashDataClass {
	/**
	 * Before creation of the cache.
	 *
	 * @param registry DashRegistry™
	 */
	default void saveInit() {
	}

	/**
	 * Before the Dashification of minecraft objects.
	 *
	 * @param registry DashRegistry™
	 */
	default void saveReload(DashRegistry registry) {
	}

	/**
	 * Before starting to serialize the cache.
	 *
	 * @param registry DashRegistry™
	 */
	default void saveApply(DashRegistry registry) {
	}

	default void reload(DashRegistry registry) {
	}

	default void apply(DashRegistry registry) {
	}

	default void serialize(DashRegistry registry) {
	}

}
