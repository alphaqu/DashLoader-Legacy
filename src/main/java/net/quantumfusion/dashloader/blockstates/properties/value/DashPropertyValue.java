package net.quantumfusion.dashloader.blockstates.properties.value;

import net.quantumfusion.dashloader.DashRegistry;

public interface DashPropertyValue {
    <K extends Comparable> K toUndash(DashRegistry registry);
}
