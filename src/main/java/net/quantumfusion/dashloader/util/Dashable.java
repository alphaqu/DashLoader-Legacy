package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.cache.DashRegistry;

public interface Dashable {

    <K> K toUndash();
}
