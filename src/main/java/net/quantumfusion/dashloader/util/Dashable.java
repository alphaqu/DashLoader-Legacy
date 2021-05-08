package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashRegistry;

public interface Dashable {
    <K> K toUndash(DashRegistry registry);
}
