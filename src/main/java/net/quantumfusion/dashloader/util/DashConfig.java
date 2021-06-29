package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.api.feature.Feature;

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

