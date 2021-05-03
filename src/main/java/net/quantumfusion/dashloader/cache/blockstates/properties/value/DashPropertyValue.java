package net.quantumfusion.dashloader.cache.blockstates.properties.value;

import net.quantumfusion.dashloader.cache.DashRegistry;

public interface DashPropertyValue {
    <K extends Comparable> K toUndash(DashRegistry registry);
}
