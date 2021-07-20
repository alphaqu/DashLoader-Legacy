package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.feature.Feature;

public class DashConfig {
	private Feature[] disabledFeatures;


	@SuppressWarnings("unused") // snakeyaml
	public DashConfig() {
	}

	public DashConfig(Feature[] disabledFeatures) {
		this.disabledFeatures = disabledFeatures;
	}

	public Feature[] getDisabledFeatures() {
		return disabledFeatures;
	}

	public void setDisabledFeatures(Feature[] disabledFeatures) {
		this.disabledFeatures = disabledFeatures;
	}
}

