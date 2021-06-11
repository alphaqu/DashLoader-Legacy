package net.quantumfusion.dashloader.data;

import net.quantumfusion.dashloader.DashRegistry;

public interface Dashable {
    <K> K toUndash(DashRegistry registry);
}
